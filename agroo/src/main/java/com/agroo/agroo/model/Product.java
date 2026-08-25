package com.agroo.agroo.model;

import com.agroo.agroo.model.enums.ProductCategory;
import com.agroo.agroo.model.enums.ProductType;
import com.agroo.agroo.model.enums.SaleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double price;

    private Double quantity;
    private String unit; // kg, liters, pieces, acres, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleType saleType;

    @Column(nullable = false)
    private String location;

    private String district;
    private String address;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "is_organic")
    private Boolean isOrganic = false;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "contact_whatsapp")
    private String contactWhatsapp;

    @Column(name = "harvest_date")
    private LocalDateTime harvestDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to add image
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    // Helper method to remove image
    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }
}