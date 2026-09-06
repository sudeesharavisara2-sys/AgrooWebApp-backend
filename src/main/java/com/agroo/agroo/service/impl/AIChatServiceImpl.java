package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.request.ChatRequest;
import com.agroo.agroo.dto.response.ChatResponse;
import com.agroo.agroo.service.AIChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AIChatServiceImpl implements AIChatService {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Fallback responses when API is not available
    private static final Map<String, String> FALLBACK_RESPONSES = new HashMap<>();

    static {
        FALLBACK_RESPONSES.put("organic farming",
                "🌱 Organic farming uses natural methods to grow crops without synthetic pesticides or fertilizers. " +
                        "It promotes soil health, biodiversity, and sustainable agriculture.");

        FALLBACK_RESPONSES.put("pest control",
                "🐛 For pest control, try these natural methods:\n" +
                        "1. Neem oil spray - mix 2 tbsp neem oil with 1 liter water\n" +
                        "2. Garlic-chili spray - blend garlic and chili with water\n" +
                        "3. Companion planting - plant marigolds, basil, or mint near crops");

        FALLBACK_RESPONSES.put("crop disease",
                "🔬 Common crop diseases in Sri Lanka:\n" +
                        "1. Blight - remove infected leaves, use copper-based fungicides\n" +
                        "2. Powdery Mildew - apply sulfur spray or baking soda solution\n" +
                        "3. Root Rot - improve drainage, avoid overwatering");

        FALLBACK_RESPONSES.put("fertilizer",
                "🧪 Fertilizer guide for Sri Lankan farms:\n" +
                        "• Organic: Compost, cow dung, green manure\n" +
                        "• Chemical: NPK (Nitrogen, Phosphorus, Potassium)\n" +
                        "• Application: Test soil first, follow recommended rates");

        FALLBACK_RESPONSES.put("market price",
                "📊 For current market prices in Sri Lanka:\n" +
                        "• Check Dambulla Economic Center daily rates\n" +
                        "• Contact local Agrarian Service Centers\n" +
                        "• Join farmers' WhatsApp groups for real-time updates");

        FALLBACK_RESPONSES.put("machinery rental",
                "🚜 Machinery rental options in Sri Lanka:\n" +
                        "• Tractors: 4WD, 2WD options available\n" +
                        "• Harvesters: Combine harvesters, paddy harvesters\n" +
                        "• Tillers: Power tillers for land preparation");

        FALLBACK_RESPONSES.put("disease",
                "🌿 Plant disease prevention tips:\n" +
                        "1. Use disease-resistant varieties\n" +
                        "2. Practice crop rotation\n" +
                        "3. Maintain proper spacing for air circulation\n" +
                        "4. Water at the base, not on leaves");

        FALLBACK_RESPONSES.put("crop",
                "🌾 Best crops for Sri Lankan farmers:\n" +
                        "• Paddy rice - major staple crop\n" +
                        "• Vegetables: Cabbage, Brinjal, Tomato, Beans\n" +
                        "• Fruits: Mango, Papaya, Banana, Pineapple\n" +
                        "• Spices: Pepper, Cinnamon, Cardamom");

        FALLBACK_RESPONSES.put("soil",
                "🪨 Soil management tips:\n" +
                        "1. Test soil pH (ideal 6.0-7.0)\n" +
                        "2. Add organic matter (compost, manure)\n" +
                        "3. Practice crop rotation\n" +
                        "4. Use cover crops to prevent erosion");

        FALLBACK_RESPONSES.put("hello",
                "🌾 Hello! Welcome to Agroo AI Assistant!\n\n" +
                        "I'm here to help you with all your farming needs. " +
                        "You can ask me about:\n" +
                        "• Organic farming techniques\n" +
                        "• Pest and disease control\n" +
                        "• Fertilizer recommendations\n" +
                        "• Market prices\n" +
                        "• Machinery rental\n" +
                        "• Crop management\n\n" +
                        "What would you like to know?");

        FALLBACK_RESPONSES.put("hi",
                "👋 Hi there! How can I help you today?\n\n" +
                        "You can ask me about farming, pests, diseases, market prices, or machinery rentals.");

        FALLBACK_RESPONSES.put("help",
                "🆘 Agroo AI Assistant - Help Menu\n\n" +
                        "Here are some things you can ask me:\n" +
                        "• 'How to grow organic vegetables?'\n" +
                        "• 'How to control pests?'\n" +
                        "• 'What is the best fertilizer?'\n" +
                        "• 'Current market prices for vegetables'\n" +
                        "• 'How to rent a tractor?'\n" +
                        "• 'How to prevent crop diseases?'\n\n" +
                        "Just type your question and I'll help you!");
    }

    @Override
    public ChatResponse processChat(ChatRequest request) {
        try {
            String message = request.getMessage().toLowerCase();

            // Log the request
            System.out.println("📝 Chat Request: " + message);

            // Check if API key is available and not default
            if (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("sk-your-api-key-here")) {
                try {
                    ChatResponse response = callOpenAI(request);
                    if (response != null && response.getReply() != null && !response.getReply().isEmpty()) {
                        System.out.println("📤 Chat Response (OpenAI): " + response.getReply().substring(0, Math.min(50, response.getReply().length())) + "...");
                        return response;
                    }
                } catch (Exception e) {
                    System.err.println("OpenAI API error: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Use fallback responses
            ChatResponse response = getFallbackResponse(request);
            System.out.println("📤 Chat Response (Fallback): " + response.getReply().substring(0, Math.min(50, response.getReply().length())) + "...");
            return response;

        } catch (Exception e) {
            System.err.println("Chat error: " + e.getMessage());
            e.printStackTrace();

            // Return error response
            return ChatResponse.builder()
                    .reply("I'm sorry, I encountered an error. Please try again later.")
                    .sessionId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .source("ERROR")
                    .success(false)
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }

    private ChatResponse callOpenAI(ChatRequest request) {
        String systemPrompt = "You are AgrooAI, a helpful agricultural assistant for Sri Lankan farmers. " +
                "Provide practical, accurate advice about farming, crops, livestock, machinery, " +
                "market prices, and sustainable agriculture. Keep responses concise and useful.";

        String userMessage = request.getMessage();
        if (request.getContext() != null && !request.getContext().isEmpty()) {
            userMessage = "Context: " + request.getContext() + "\nQuestion: " + userMessage;
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", Arrays.asList(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
        ));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 500);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                apiUrl,
                entity,
                String.class
        );

        String reply = extractReplyFromOpenAI(response.getBody());

        return ChatResponse.builder()
                .reply(reply)
                .sessionId(request.getSessionId() != null ? request.getSessionId() : UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .source("OPENAI")
                .success(true)
                .message("Response generated")
                .build();
    }

    private String extractReplyFromOpenAI(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).path("message");
                return message.path("content").asText();
            }
        } catch (Exception e) {
            System.err.println("Error parsing OpenAI response: " + e.getMessage());
            return "I'm sorry, I couldn't process your request. Please try again.";
        }
        return "I'm sorry, I couldn't understand your question. Could you rephrase?";
    }

    private ChatResponse getFallbackResponse(ChatRequest request) {
        String message = request.getMessage().toLowerCase();
        String reply = findRelevantResponse(message);

        if (reply == null) {
            reply = "🌾 Welcome to Agroo AI Assistant!\n\n" +
                    "I can help you with:\n" +
                    "• 🌱 Organic farming advice\n" +
                    "• 🐛 Pest and disease control\n" +
                    "• 🧪 Fertilizer recommendations\n" +
                    "• 📊 Market prices and trends\n" +
                    "• 🚜 Machinery rental information\n" +
                    "• 🌿 Crop management tips\n" +
                    "• 🪨 Soil health and management\n\n" +
                    "What would you like to know about today?";
        }

        return ChatResponse.builder()
                .reply(reply)
                .sessionId(request.getSessionId() != null ? request.getSessionId() : UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .source("FARMER_GUIDE")
                .success(true)
                .message("Response generated from farming guide")
                .build();
    }

    private String findRelevantResponse(String message) {
        for (Map.Entry<String, String> entry : FALLBACK_RESPONSES.entrySet()) {
            if (message.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public ChatResponse getFarmingAdvice(String query) {
        ChatRequest request = new ChatRequest();
        request.setMessage(query);
        request.setContext("farming_advice");
        return processChat(request);
    }

    @Override
    public ChatResponse getMarketPrices(String query) {
        ChatRequest request = new ChatRequest();
        request.setMessage(query);
        request.setContext("market_prices");
        return processChat(request);
    }

    @Override
    public ChatResponse getDiseaseAdvice(String query) {
        ChatRequest request = new ChatRequest();
        request.setMessage(query);
        request.setContext("disease_advice");
        return processChat(request);
    }
}