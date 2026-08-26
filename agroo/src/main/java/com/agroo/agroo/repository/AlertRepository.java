package com.agroo.agroo.repository;

import com.agroo.agroo.model.Alert;
import com.agroo.agroo.model.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    // Find active alerts with pagination
    Page<Alert> findByIsActiveTrue(Pageable pageable);

    // Find active alerts (without pagination - for list)
    List<Alert> findByIsActiveTrue();

    // Find urgent active alerts
    List<Alert> findByIsActiveTrueAndIsUrgentTrue();

    // Find by alert type
    List<Alert> findByAlertType(AlertType alertType);

    // Find by alert type with pagination
    Page<Alert> findByAlertType(AlertType alertType, Pageable pageable);

    // Find active alerts by type
    List<Alert> findByIsActiveTrueAndAlertType(AlertType alertType);

    // Find urgent alerts by type
    List<Alert> findByIsUrgentTrueAndAlertType(AlertType alertType);

    // Count active alerts
    long countByIsActiveTrue();

    // Count urgent alerts
    long countByIsUrgentTrue();

    // Count by alert type
    long countByAlertType(AlertType alertType);
}