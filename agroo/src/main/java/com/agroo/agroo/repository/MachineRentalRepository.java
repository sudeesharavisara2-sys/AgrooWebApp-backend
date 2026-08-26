package com.agroo.agroo.repository;

import com.agroo.agroo.model.MachineRental;
import com.agroo.agroo.model.enums.MachineStatus;
import com.agroo.agroo.model.enums.MachineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineRentalRepository extends JpaRepository<MachineRental, Long> {

    // Find by owner
    Page<MachineRental> findByOwnerId(Long ownerId, Pageable pageable);
    List<MachineRental> findByOwnerId(Long ownerId);

    // Find by machine type
    Page<MachineRental> findByMachineType(MachineType machineType, Pageable pageable);
    List<MachineRental> findByMachineType(MachineType machineType);

    // Find by status
    Page<MachineRental> findByStatus(MachineStatus status, Pageable pageable);
    List<MachineRental> findByStatus(MachineStatus status);

    // Find available machines
    Page<MachineRental> findByIsAvailableTrue(Pageable pageable);
    List<MachineRental> findByIsAvailableTrue();

    // Search by name - JPQL
    @Query("SELECT m FROM MachineRental m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<MachineRental> searchByName(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM MachineRental m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MachineRental> searchByName(@Param("keyword") String keyword);

    // Search by location - JPQL
    @Query("SELECT m FROM MachineRental m WHERE LOWER(m.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    Page<MachineRental> searchByLocation(@Param("location") String location, Pageable pageable);

    @Query("SELECT m FROM MachineRental m WHERE LOWER(m.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<MachineRental> searchByLocation(@Param("location") String location);

    // ============================================================
    // ADVANCED SEARCH - JPQL ONLY (NO NATIVE QUERIES!)
    // ============================================================
    @Query("SELECT m FROM MachineRental m WHERE " +
            "(:machineType IS NULL OR m.machineType = :machineType) AND " +
            "(:status IS NULL OR m.status = :status) AND " +
            "(:minPrice IS NULL OR m.pricePerDay >= :minPrice) AND " +
            "(:maxPrice IS NULL OR m.pricePerDay <= :maxPrice) AND " +
            "(:location IS NULL OR LOWER(m.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "m.isAvailable = true")
    Page<MachineRental> advancedSearch(
            @Param("machineType") MachineType machineType,
            @Param("status") MachineStatus status,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("location") String location,
            Pageable pageable
    );

    // Count by owner
    Long countByOwnerId(Long ownerId);

    // Find most viewed
    @Query("SELECT m FROM MachineRental m ORDER BY m.viewCount DESC")
    Page<MachineRental> findMostViewed(Pageable pageable);
}