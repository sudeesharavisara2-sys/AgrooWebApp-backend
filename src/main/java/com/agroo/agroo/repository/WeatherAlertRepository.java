package com.agroo.agroo.repository;

import com.agroo.agroo.model.WeatherAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WeatherAlertRepository extends JpaRepository<WeatherAlert, Long> {

    List<WeatherAlert> findByIsActiveTrueAndIsSentFalse();

    List<WeatherAlert> findByLocationContainingIgnoreCase(String location);

    @Modifying
    @Transactional
    @Query("UPDATE WeatherAlert w SET w.isSent = true, w.sentAt = :sentAt WHERE w.id = :id")
    void markAsSent(Long id, LocalDateTime sentAt);

    long countByIsActiveTrueAndIsSentFalse();
}