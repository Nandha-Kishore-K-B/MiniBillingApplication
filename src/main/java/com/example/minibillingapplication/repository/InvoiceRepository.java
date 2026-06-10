package com.example.minibillingapplication.repository;


import com.example.minibillingapplication.dto.MonthlyRevenue;
import com.example.minibillingapplication.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Generates a monthly breakdown of net revenue per tax region.
    // The Hibernate aspect injects "WHERE i.tenant_id = ?" automatically behind the scenes!
    @Query("SELECT i FROM Invoice i JOIN FETCH i.lineItems WHERE i.id = :id")
    Optional<Invoice> findByIdWithLineItems(@Param("id") Long id);
    List<Invoice> findAllByStatus(String status);
}