package com.agroo.agroo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String alertType;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false, length = 1000)
    private String message;

    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private Double rainfall;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_sent")
    private Boolean isSent = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusHours(24);
        }
    }
}