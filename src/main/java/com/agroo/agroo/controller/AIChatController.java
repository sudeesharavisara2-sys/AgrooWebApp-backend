package com.agroo.agroo.controller;

import com.agroo.agroo.dto.request.ChatRequest;
import com.agroo.agroo.dto.response.ChatResponse;
import com.agroo.agroo.service.AIChatService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class AIChatController {

    private final AIChatService aiChatService;

    @PostConstruct
    public void init() {
        System.out.println("✅ AIChatController initialized with service: " + aiChatService);
    }

    // General Chat
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        System.out.println("📨 Received chat request: " + request.getMessage());
        ChatResponse response = aiChatService.processChat(request);
        System.out.println("📨 Sending response: " + response.getReply());
        return ResponseEntity.ok(response);
    }

    // Farming Advice
    @PostMapping("/farming-advice")
    public ResponseEntity<ChatResponse> getFarmingAdvice(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(aiChatService.getFarmingAdvice(request.getMessage()));
    }

    // Market Prices
    @PostMapping("/market-prices")
    public ResponseEntity<ChatResponse> getMarketPrices(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(aiChatService.getMarketPrices(request.getMessage()));
    }

    // Disease Advice
    @PostMapping("/disease-advice")
    public ResponseEntity<ChatResponse> getDiseaseAdvice(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(aiChatService.getDiseaseAdvice(request.getMessage()));
    }

    // Quick Questions (GET)
    @GetMapping("/ask")
    public ResponseEntity<ChatResponse> askQuestion(@RequestParam String question) {
        ChatRequest request = new ChatRequest();
        request.setMessage(question);
        return ResponseEntity.ok(aiChatService.processChat(request));
    }

    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Chat API is working!");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // Ping endpoint
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Chat API is alive!");
    }
}