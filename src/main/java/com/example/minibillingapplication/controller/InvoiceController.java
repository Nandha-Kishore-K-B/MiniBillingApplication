package com.example.minibillingapplication.controller;

import com.example.minibillingapplication.dto.MonthlyRevenue;
import com.example.minibillingapplication.entity.Invoice;
import com.example.minibillingapplication.service.InvoiceService;
import com.example.minibillingapplication.service.PdfService;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@Getter
@Setter
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PdfService pdfService;

    public InvoiceController(InvoiceService invoiceService, PdfService pdfService) {
        this.invoiceService = invoiceService;
        this.pdfService = pdfService;
    }

    // Only ADMIN and MANAGER roles can create invoices
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        Invoice created = invoiceService.createInvoice(invoice);
        return ResponseEntity.ok(created);
    }

    // Anyone in the business can view a specific invoice
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_VIEWER')")
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    // Only Admins can run financial reports
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/reports/revenue")
    public ResponseEntity<List<MonthlyRevenue>> getRevenueReport() {
        return ResponseEntity.ok(invoiceService.getMonthlyRevenueReport());
    }

    // Fixed: Removed the stray extra brace that was here closing the class early!

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Invoice> payInvoice(@PathVariable Long id) {
        Invoice updatedInvoice = invoiceService.markInvoiceAsPaid(id);
        return ResponseEntity.ok(updatedInvoice);
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {

        // 1. SECURITY CHECK: Fetch the invoice first!
        invoiceService.getInvoiceById(id);

        // 2. If the user owns the invoice, grab the file
        byte[] pdfBytes = pdfService.getInvoicePdf(id);

        // 3. Set the headers so the browser knows it's a PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "invoice_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}