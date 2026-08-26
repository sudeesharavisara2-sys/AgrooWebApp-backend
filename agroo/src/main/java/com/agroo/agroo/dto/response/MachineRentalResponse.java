package com.agroo.agroo.dto.response;

import com.agroo.agroo.model.enums.MachineStatus;
import com.agroo.agroo.model.enums.MachineType;
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
public class MachineRentalResponse {
    private Long id;
    private String name;
    private String description;
    private MachineType machineType;
    private Double pricePerDay;
    private Double pricePerHour;
    private Double pricePerAcre;
    private String location;
    private String district;
    private String contactPhone;
    private String contactWhatsapp;
    private Boolean isAvailable;
    private MachineStatus status;
    private Boolean isVerified;
    private Integer viewCount;
    private Integer yearOfManufacture;
    private String brand;
    private String model;
    private String fuelType;
    private Double horsePower;
    private List<String> features;
    private OwnerInfo owner;
    private List<MachineImageInfo> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnerInfo {
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
    public static class MachineImageInfo {
        private Long id;
        private String imageUrl;
        private Boolean isPrimary;
        private Integer displayOrder;
    }
}