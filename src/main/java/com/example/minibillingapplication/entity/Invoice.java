package com.example.minibillingapplication.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "invoices")
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    private LocalDate issueDate;

    // Default to DRAFT state
    private String status = "DRAFT";

    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Prevents infinite recursion when serializing to JSON
    private List<InvoiceLineItem> lineItems = new ArrayList<>();

    public Invoice() {}

    public Invoice(Long customerId, LocalDate issueDate, BigDecimal totalAmount, String tenantId) {
        this.customerId = customerId;
        this.issueDate = issueDate;
        this.totalAmount = totalAmount;
        this.setTenantId(tenantId);
    }

    public void addLineItem(InvoiceLineItem item) {
        lineItems.add(item);
        item.setInvoice(this);
    }

}