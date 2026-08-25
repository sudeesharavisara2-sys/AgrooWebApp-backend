package com.agroo.agroo.dto.request;

import com.agroo.agroo.model.enums.ProductCategory;
import com.agroo.agroo.model.enums.ProductType;
import com.agroo.agroo.model.enums.SaleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    private Double quantity;
    private String unit;

    @NotNull(message = "Category is required")
    private ProductCategory category;

    @NotNull(message = "Product type is required")
    private ProductType productType;

    @NotNull(message = "Sale type is required")
    private SaleType saleType;

    @NotBlank(message = "Location is required")
    private String location;

    private String district;
    private String address;

    private Boolean isAvailable = true;
    private Boolean isOrganic = false;

    private String contactPhone;
    private String contactWhatsapp;

    private String harvestDate;
    private String expiryDate;
}