package com.example.minibillingapplication;

import org.springframework.boot.SpringApplication;

public class TestMiniBillingApplication {

    public static void main(String[] args) {
        SpringApplication.from(MiniBillingApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
