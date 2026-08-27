package com.agroo.agroo.dto.response;

import com.agroo.agroo.model.enums.ProductCategory;
import com.agroo.agroo.model.enums.ProductType;
import com.agroo.agroo.model.enums.SaleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double quantity;
    private String unit;
    private ProductCategory category;
    private ProductType productType;
    private SaleType saleType;
    private String location;
    private String district;
    private String address;
    private Boolean isAvailable;
    private Boolean isOrganic;
    private Boolean isVerified;
    private String contactPhone;
    private String contactWhatsapp;
    private LocalDateTime harvestDate;
    private LocalDateTime expiryDate;
    private Integer viewCount;
    private UserInfo farmer;
    private List<ProductImageInfo> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String fullName;
        private String phoneNumber;
        private String profileImageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageInfo {
        private Long id;
        private String imageUrl;
        private Boolean isPrimary;
        private Integer displayOrder;
    }
}