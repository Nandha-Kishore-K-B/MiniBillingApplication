package com.example.minibillingapplication.listener;


import com.example.minibillingapplication.config.TenantContext;
import com.example.minibillingapplication.entity.Invoice;
import com.example.minibillingapplication.event.InvoiceCreatedEvent;
import com.example.minibillingapplication.repository.InvoiceRepository;
import com.example.minibillingapplication.service.PdfService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.FileOutputStream;

@Component
public class InvoiceNotificationListener {

    private final PdfService pdfService;
    private final InvoiceRepository invoiceRepository;

    public InvoiceNotificationListener(PdfService pdfService, InvoiceRepository invoiceRepository) {
        this.pdfService = pdfService;
        this.invoiceRepository = invoiceRepository;
    }

    // Ensures this triggers ONLY after the DB transaction successfully commits
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInvoiceCreated(InvoiceCreatedEvent event) {
        // Set the context for this background thread
        TenantContext.setCurrentTenant(event.tenantId());

        try {
            Invoice invoice = invoiceRepository.findByIdWithLineItems(event.invoiceId())
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));
            byte[] pdfBytes = pdfService.generateInvoicePdf(invoice);

            // For testing: Save to local disk. (In prod, upload to AWS S3 or send via email)
            String filename = "invoice_" + invoice.getId() + ".pdf";
            try (FileOutputStream fos = new FileOutputStream(filename)) {
                fos.write(pdfBytes);
                System.out.println("Background Task: Successfully generated " + filename);
            }

        } catch (Exception e) {
            System.err.println("Failed to generate PDF: " + e.getMessage());
        } finally {
            // ALWAYS clear the context
            TenantContext.clear();
        }
    }
}