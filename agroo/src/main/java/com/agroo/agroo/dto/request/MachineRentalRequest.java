package com.agroo.agroo.dto.request;

import com.agroo.agroo.model.enums.MachineStatus;
import com.agroo.agroo.model.enums.MachineType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineRentalRequest {

    @NotBlank(message = "Machine name is required")
    private String name;

    private String description;

    @NotNull(message = "Machine type is required")
    private MachineType machineType;

    @NotNull(message = "Price per day is required")
    @Positive(message = "Price must be positive")
    private Double pricePerDay;

    private Double pricePerHour;
    private Double pricePerAcre;

    @NotBlank(message = "Location is required")
    private String location;

    private String district;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    private String contactWhatsapp;

    private MachineStatus status = MachineStatus.AVAILABLE;

    private Integer yearOfManufacture;
    private String brand;
    private String model;
    private String fuelType;
    private Double horsePower;

    private List<String> features;
}