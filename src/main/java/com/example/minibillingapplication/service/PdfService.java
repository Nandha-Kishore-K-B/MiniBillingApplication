package com.example.minibillingapplication.service;


import com.example.minibillingapplication.entity.Invoice;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PdfService {

    private final TemplateEngine templateEngine;

    public PdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generateInvoicePdf(Invoice invoice) {
        try {
            // 1. Pass the invoice data to the HTML template
            Context context = new Context();
            context.setVariable("invoice", invoice);

            // 2. Process the HTML template (looks for invoice-template.html in resources)
            String htmlContent = templateEngine.process("invoice-template", context);

            // 3. Render the HTML to PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();

            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF for Invoice: " + invoice.getId(), e);
        }
    }
    public byte[] getInvoicePdf(Long invoiceId) {
        try {
            Path pdfPath = Paths.get("invoice_" + invoiceId + ".pdf");

            if (!Files.exists(pdfPath)) {
                throw new RuntimeException("PDF file not found on server.");
            }

            return Files.readAllBytes(pdfPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read PDF file.", e);
        }
    }
}