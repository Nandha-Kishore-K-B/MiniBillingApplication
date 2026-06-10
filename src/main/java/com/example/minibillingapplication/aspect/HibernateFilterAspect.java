package com.example.minibillingapplication.aspect;

import com.example.minibillingapplication.config.TenantContext;
import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HibernateFilterAspect {

    private final EntityManager entityManager;

    public HibernateFilterAspect(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Intercepts EVERY method executed inside the repository package
    @Before("execution(* com.zoho.mini.repository.*.*(..))")
    public void enableTenantFilter() {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) {
            // Unwrap the JPA EntityManager to get the native Hibernate Session
            Session session = entityManager.unwrap(Session.class);

            // Enable the filter defined on our BaseEntity
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        }
    }
}