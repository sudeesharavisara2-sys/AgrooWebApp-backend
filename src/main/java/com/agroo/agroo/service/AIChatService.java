package com.agroo.agroo.service;

import com.agroo.agroo.dto.request.ChatRequest;
import com.agroo.agroo.dto.response.ChatResponse;

public interface AIChatService {
    ChatResponse processChat(ChatRequest request);
    ChatResponse getFarmingAdvice(String query);
    ChatResponse getMarketPrices(String query);
    ChatResponse getDiseaseAdvice(String query);
}