package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.request.PriceRequest;
import com.agroo.agroo.dto.response.PriceResponse;
import com.agroo.agroo.model.Price;
import com.agroo.agroo.model.User;
import com.agroo.agroo.repository.PriceRepository;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PriceResponse addPrice(PriceRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Price price = new Price();
        price.setProductName(request.getProductName());
        price.setLocation(request.getLocation());
        price.setPrice(request.getPrice());
        price.setUnit(request.getUnit());
        price.setUpdatedBy(user);

        if (request.getPriceDate() != null && !request.getPriceDate().isEmpty()) {
            price.setPriceDate(LocalDate.parse(request.getPriceDate(), DateTimeFormatter.ISO_DATE));
        }

        price = priceRepository.save(price);
        return mapToResponse(price);
    }

    @Override
    @Transactional
    public PriceResponse updatePrice(Long priceId, PriceRequest request, String username) {
        Price price = priceRepository.findById(priceId)
                .orElseThrow(() -> new RuntimeException("Price not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        price.setProductName(request.getProductName());
        price.setLocation(request.getLocation());
        price.setPrice(request.getPrice());
        price.setUnit(request.getUnit());
        price.setUpdatedBy(user);

        if (request.getPriceDate() != null && !request.getPriceDate().isEmpty()) {
            price.setPriceDate(LocalDate.parse(request.getPriceDate(), DateTimeFormatter.ISO_DATE));
        }

        price = priceRepository.save(price);
        return mapToResponse(price);
    }

    @Override
    public List<PriceResponse> getAllPrices() {
        return priceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PriceResponse> getLatestPrices() {
        return priceRepository.findLatestPrices().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PriceResponse> getPricesByProduct(String productName) {
        return priceRepository.findByProductNameContainingIgnoreCase(productName).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Map<String, Double>> comparePrices(String productName, List<String> locations) {
        Map<String, Map<String, Double>> result = new HashMap<>();

        for (String location : locations) {
            List<Price> prices = priceRepository.findByProductNameAndLocationOrderByPriceDateDesc(productName, location);
            if (!prices.isEmpty()) {
                Map<String, Double> priceMap = new HashMap<>();
                priceMap.put("latestPrice", prices.get(0).getPrice());
                result.put(location, priceMap);
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void deletePrice(Long priceId) {
        Price price = priceRepository.findById(priceId)
                .orElseThrow(() -> new RuntimeException("Price not found"));
        priceRepository.delete(price);
    }

    private PriceResponse mapToResponse(Price price) {
        return PriceResponse.builder()
                .id(price.getId())
                .productName(price.getProductName())
                .location(price.getLocation())
                .price(price.getPrice())
                .unit(price.getUnit())
                .priceDate(price.getPriceDate())
                .updatedBy(price.getUpdatedBy() != null ?
                        PriceResponse.UserInfo.builder()
                                .id(price.getUpdatedBy().getId())
                                .username(price.getUpdatedBy().getUsername())
                                .fullName(price.getUpdatedBy().getFullName())
                                .build() : null)
                .createdAt(price.getCreatedAt())
                .updatedAt(price.getUpdatedAt())
                .build();
    }
}