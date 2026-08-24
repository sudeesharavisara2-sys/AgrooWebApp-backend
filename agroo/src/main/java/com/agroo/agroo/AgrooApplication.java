package com.agroo.agroo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AgrooApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgrooApplication.class, args);
        System.out.println("\n🌾 ==========================================");
        System.out.println("   Agroo Application Started Successfully!");
        System.out.println("   🔗 Visit: http://localhost:8081");
        System.out.println("   📋 API Test: http://localhost:8081/api/test");
        System.out.println("   🔐 Auth: http://localhost:8081/api/auth");
        System.out.println("🌾 ==========================================\n");
    }
}