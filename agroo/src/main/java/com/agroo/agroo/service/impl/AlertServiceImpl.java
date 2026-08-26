package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.request.AlertRequest;
import com.agroo.agroo.dto.response.AlertResponse;
import com.agroo.agroo.model.Alert;
import com.agroo.agroo.model.User;
import com.agroo.agroo.model.enums.AlertType;
import com.agroo.agroo.repository.AlertRepository;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AlertResponse createAlert(AlertRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Alert alert = new Alert();
        alert.setTitle(request.getTitle());
        alert.setContent(request.getContent());
        alert.setAlertType(request.getAlertType());
        alert.setLocation(request.getLocation());
        alert.setIsUrgent(request.getIsUrgent() != null ? request.getIsUrgent() : false);
        alert.setCreatedBy(user);
        alert.setIsActive(true);

        if (request.getExpiresAt() != null && !request.getExpiresAt().isEmpty()) {
            alert.setExpiresAt(LocalDateTime.parse(request.getExpiresAt(), DateTimeFormatter.ISO_DATE_TIME));
        }

        alert = alertRepository.save(alert);
        return mapToResponse(alert);
    }

    @Override
    @Transactional
    public AlertResponse updateAlert(Long alertId, AlertRequest request, String username) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        alert.setTitle(request.getTitle());
        alert.setContent(request.getContent());
        alert.setAlertType(request.getAlertType());
        alert.setLocation(request.getLocation());
        alert.setIsUrgent(request.getIsUrgent() != null ? request.getIsUrgent() : false);
        alert.setCreatedBy(user);

        if (request.getExpiresAt() != null && !request.getExpiresAt().isEmpty()) {
            alert.setExpiresAt(LocalDateTime.parse(request.getExpiresAt(), DateTimeFormatter.ISO_DATE_TIME));
        }

        alert = alertRepository.save(alert);
        return mapToResponse(alert);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertResponse> getAllAlerts(Pageable pageable) {
        return alertRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getActiveAlerts() {
        // Fixed: Use PageRequest.of() instead of lambda
        Page<Alert> alertPage = alertRepository.findByIsActiveTrue(PageRequest.of(0, 100));
        return alertPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getUrgentAlerts() {
        return alertRepository.findByIsActiveTrueAndIsUrgentTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsByType(AlertType type) {
        return alertRepository.findByAlertType(type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AlertResponse deactivateAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        alert.setIsActive(false);
        alert = alertRepository.save(alert);
        return mapToResponse(alert);
    }

    @Override
    @Transactional
    public void deleteAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alertRepository.delete(alert);
    }

    private AlertResponse mapToResponse(Alert alert) {
        // Force initialize the createdBy proxy
        User createdBy = alert.getCreatedBy();

        return AlertResponse.builder()
                .id(alert.getId())
                .title(alert.getTitle())
                .content(alert.getContent())
                .alertType(alert.getAlertType())
                .location(alert.getLocation())
                .isActive(alert.getIsActive())
                .isUrgent(alert.getIsUrgent())
                .createdBy(createdBy != null ?
                        AlertResponse.UserInfo.builder()
                                .id(createdBy.getId())
                                .username(createdBy.getUsername())
                                .fullName(createdBy.getFullName())
                                .build() : null)
                .expiresAt(alert.getExpiresAt())
                .createdAt(alert.getCreatedAt())
                .updatedAt(alert.getUpdatedAt())
                .build();
    }
}