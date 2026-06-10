package com.example.minibillingapplication.security;

import org.springframework.stereotype.Service;

@Service
public class JwtService {

    // In a real app, use io.jsonwebtoken.Jwts to parse and verify the token signature.
    // For this prototype, we will assume a simple mock implementation.

    public String extractTenantId(String token) {
        // Mock logic: assuming token is something like "tenant-id_role_username"
        if (token != null && token.contains("_")) {
            return token.split("_")[0];
        }
        return "default-tenant";
    }

    public String extractRole(String token) {
        if (token != null && token.contains("_")) {
            return "ROLE_" + token.split("_")[1].toUpperCase();
        }
        return "ROLE_VIEWER";
    }
}