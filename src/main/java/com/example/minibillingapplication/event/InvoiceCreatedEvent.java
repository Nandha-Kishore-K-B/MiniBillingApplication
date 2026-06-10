package com.example.minibillingapplication.event;


// A modern Java Record is perfect for immutable event data
public record InvoiceCreatedEvent(Long invoiceId, String tenantId) {
}