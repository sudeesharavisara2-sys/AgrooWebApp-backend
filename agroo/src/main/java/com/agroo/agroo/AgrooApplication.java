package com.agroo.agroo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgrooApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgrooApplication.class, args);
        System.out.println("🌾 Agroo Application Started Successfully!");
        System.out.println("📌 Visit: http://localhost:8080");
        System.out.println("🔗 API Test: http://localhost:8080/api/test");
    }
}