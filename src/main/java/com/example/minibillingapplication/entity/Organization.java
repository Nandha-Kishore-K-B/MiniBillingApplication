package com.example.minibillingapplication.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @Column(name = "tenant_id")
    private String tenantId;

    private String name;
    private String currencyCode;

    // Default constructor for JPA
    public Organization() {}

    public Organization(String tenantId, String name, String currencyCode) {
        this.tenantId = tenantId;
        this.name = name;
        this.currencyCode = currencyCode;
    }

}