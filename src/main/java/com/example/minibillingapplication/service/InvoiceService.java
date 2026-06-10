package com.example.minibillingapplication.service;

import com.example.minibillingapplication.config.TenantContext;
import com.example.minibillingapplication.dto.MonthlyRevenue;
import com.example.minibillingapplication.entity.Invoice;
import com.example.minibillingapplication.event.InvoiceCreatedEvent;
import com.example.minibillingapplication.repository.InvoiceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InvoiceService(InvoiceRepository invoiceRepository, ApplicationEventPublisher eventPublisher) {
        this.invoiceRepository = invoiceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        String currentTenant = TenantContext.getCurrentTenant();

        // 1. Explicitly set the tenant ID on the root entity
        invoice.setTenantId(currentTenant);

        // 2. Ensure all nested line items also belong to this tenant
        if (invoice.getLineItems() != null) {
            invoice.getLineItems().forEach(item -> {
                item.setInvoice(invoice);
                item.setTenantId(currentTenant);
            });
        }

        // 3. Save to the database atomically
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // 4. Fire the async event (Triggers PDF generation / Emailing)
        eventPublisher.publishEvent(new InvoiceCreatedEvent(savedInvoice.getId(), currentTenant));

        return savedInvoice;
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found or access denied."));
    }

    public List<Invoice> getAllInvoices() {
        // Connected to your controller's GET all endpoint
        return invoiceRepository.findAll();
    }

    public Invoice markInvoiceAsPaid(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));

        invoice.setStatus("PAID");
        return invoiceRepository.save(invoice);
    }

    public List<MonthlyRevenue> getMonthlyRevenueReport() {
        // 1. Fetch ONLY invoices marked as PAID
        List<Invoice> paidInvoices = invoiceRepository.findAllByStatus("PAID");

        // 2. Group the invoices by "YYYY-MM" and sum their total amounts
        Map<String, Double> revenueByMonth = paidInvoices.stream()
                .collect(Collectors.groupingBy(
                        invoice -> {
                            LocalDate date = LocalDate.parse(invoice.getIssueDate().toString());
                            return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                        },
                        // Fixed unboxing adapter
                        Collectors.summingDouble(invoice -> invoice.getTotalAmount().doubleValue())
                ));

        // 3. Convert the grouped Map into our MonthlyRevenue DTO list and sort it chronologically
        return revenueByMonth.entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("-");
                    return new MonthlyRevenue(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]),
                            entry.getValue()
                    );
                })
                .sorted(Comparator.comparing(MonthlyRevenue::getYear)
                        .thenComparing(MonthlyRevenue::getMonth))
                .collect(Collectors.toList());
    }
}