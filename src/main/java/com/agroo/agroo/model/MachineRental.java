package com.agroo.agroo.model;

import com.agroo.agroo.model.enums.MachineStatus;
import com.agroo.agroo.model.enums.MachineType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "machine_rentals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineRental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MachineType machineType;

    @Column(nullable = false)
    private Double pricePerDay;

    private Double pricePerHour;

    private Double pricePerAcre;

    @Column(nullable = false)
    private String location;

    private String district;

    @Column(nullable = false)
    private String contactPhone;

    private String contactWhatsapp;

    @Column(nullable = false)
    private Boolean isAvailable = true;

    @Enumerated(EnumType.STRING)
    private MachineStatus status = MachineStatus.AVAILABLE;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;

    private String brand;

    private String model;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "horse_power")
    private Double horsePower;

    // Changed to EAGER to fix lazy loading issue
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "machine_features", joinColumns = @JoinColumn(name = "machine_id"))
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    @OneToMany(mappedBy = "machineRental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MachineImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

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
    public void addImage(MachineImage image) {
        images.add(image);
        image.setMachineRental(this);
    }

    // Helper method to remove image
    public void removeImage(MachineImage image) {
        images.remove(image);
        image.setMachineRental(null);
    }
}