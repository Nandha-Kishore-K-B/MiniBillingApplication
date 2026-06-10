package com.example.minibillingapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // Required for our Event-Driven PDF generation later
public class MiniBillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniBillingApplication.class, args);
    }
}