package com.example.minibillingapplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String billingAddress;
    private String taxRegion;

    public Customer() {}

    public Customer(String name, String billingAddress, String taxRegion, String tenantId) {
        this.name = name;
        this.billingAddress = billingAddress;
        this.taxRegion = taxRegion;
        this.setTenantId(tenantId);
    }

}