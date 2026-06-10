package com.example.minibillingapplication.integration;
/**
import com.example.minibillingapplication.config.TenantContext;
import com.example.minibillingapplication.entity.Customer;
import com.example.minibillingapplication.entity.Invoice;
import com.example.minibillingapplication.repository.CustomerRepository;
import com.example.minibillingapplication.repository.InvoiceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
class DataIsolationIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private static final String TENANT_A = "zoho-corp";
    private static final String TENANT_B = "acme-inc";

    @BeforeEach
    void setupData() {
        // Create Data for Zoho
        TenantContext.setCurrentTenant(TENANT_A);
        Customer custA = customerRepository.save(new Customer("Tech Corp", "NY", "EAST", TENANT_A));
        invoiceRepository.save(new Invoice(custA.getId(), LocalDate.now(), new BigDecimal("1000"), TENANT_A));

        // Create Data for Acme
        TenantContext.setCurrentTenant(TENANT_B);
        Customer custB = customerRepository.save(new Customer("Desert LLC", "NV", "WEST", TENANT_B));
        invoiceRepository.save(new Invoice(custB.getId(), LocalDate.now(), new BigDecimal("500"), TENANT_B));

        TenantContext.clear();
    }

    @AfterEach
    void cleanUp() {
        invoiceRepository.deleteAll();
        customerRepository.deleteAll();
        TenantContext.clear();
    }

    @Test
    void testTenantACannotSeeTenantBData() {
        // Arrange: Simulate user from Zoho Corp logging in
        TenantContext.setCurrentTenant(TENANT_A);

        // Act: We call findAll(). The Aspect should intercept this!
        List<Invoice> results = invoiceRepository.findAll();

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTenantId()).isEqualTo(TENANT_A);

        // Ensure absolutely no Acme Inc data leaked
        boolean leakDetected = results.stream().anyMatch(inv -> inv.getTenantId().equals(TENANT_B));
        assertThat(leakDetected).isFalse();
    }

    @Test
    void testSystemFailsSecurelyWithoutTenantContext() {
        // If a developer forgets to set the context, the system should crash, not leak data.
        TenantContext.clear();

        assertThrows(Exception.class, () -> {
            invoiceRepository.findAll();
        });
    }
}**/