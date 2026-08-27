package com.agroo.agroo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Agroo Agricultural Platform!");
        response.put("status", "Running");
        response.put("version", "1.0.0");
        return response;
    }

    @GetMapping("/api/test")
    public Map<String, String> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "API is working!");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return response;
    }
}