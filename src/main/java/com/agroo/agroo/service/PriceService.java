package com.agroo.agroo.service;

import com.agroo.agroo.dto.request.PriceRequest;
import com.agroo.agroo.dto.response.PriceResponse;

import java.util.List;
import java.util.Map;

public interface PriceService {
    PriceResponse addPrice(PriceRequest request, String username);
    PriceResponse updatePrice(Long priceId, PriceRequest request, String username);
    List<PriceResponse> getAllPrices();
    List<PriceResponse> getLatestPrices();
    List<PriceResponse> getPricesByProduct(String productName);
    Map<String, Map<String, Double>> comparePrices(String productName, List<String> locations);
    void deletePrice(Long priceId);
}