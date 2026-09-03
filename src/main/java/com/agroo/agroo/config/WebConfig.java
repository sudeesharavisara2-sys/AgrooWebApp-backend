package com.agroo.agroo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get the absolute path to the uploads folder
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "products" + File.separator;
        String uploadPath = "file:" + uploadDir.replace("\\", "/");

        System.out.println("=========================================================");
        System.out.println("📁 Upload directory: " + uploadDir);
        System.out.println("📁 MAPPING /uploads/** TO -> " + uploadPath);
        System.out.println("=========================================================");

        // Register resource handler for /uploads/**
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600);

        // Also register for /uploads/products/**
        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600);
    }
}