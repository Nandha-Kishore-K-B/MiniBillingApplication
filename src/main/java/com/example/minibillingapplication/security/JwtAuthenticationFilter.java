package com.example.minibillingapplication.security;

import com.example.minibillingapplication.config.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            String tenantId = jwtService.extractTenantId(token);
            String role = jwtService.extractRole(token);

            // 1. Set Multi-Tenancy Context
            TenantContext.setCurrentTenant(tenantId);

            // 2. Set Spring Security Context (for RBAC @PreAuthorize annotations)
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    "user", null, Collections.singletonList(authority));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        try {
            // Continue the request down the chain
            chain.doFilter(request, response);
        } finally {
            // CRITICAL: Always clear ThreadLocals to prevent data leaks in connection pools
            TenantContext.clear();
            SecurityContextHolder.clearContext();
        }
    }
}