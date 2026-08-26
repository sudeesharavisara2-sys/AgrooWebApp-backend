package com.agroo.agroo.service;

import com.agroo.agroo.dto.request.AlertRequest;
import com.agroo.agroo.dto.response.AlertResponse;
import com.agroo.agroo.model.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AlertService {
    AlertResponse createAlert(AlertRequest request, String username);
    AlertResponse updateAlert(Long alertId, AlertRequest request, String username);
    Page<AlertResponse> getAllAlerts(Pageable pageable);
    List<AlertResponse> getActiveAlerts();
    List<AlertResponse> getUrgentAlerts();
    List<AlertResponse> getAlertsByType(AlertType type);
    AlertResponse deactivateAlert(Long alertId);
    void deleteAlert(Long alertId);
}