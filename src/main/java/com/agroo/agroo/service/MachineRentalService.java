package com.agroo.agroo.service;

import com.agroo.agroo.dto.request.MachineRentalRequest;
import com.agroo.agroo.dto.response.MachineRentalResponse;
import com.agroo.agroo.model.enums.MachineStatus;
import com.agroo.agroo.model.enums.MachineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MachineRentalService {
    // Create
    MachineRentalResponse createMachineRental(MachineRentalRequest request, List<MultipartFile> images, String username);

    // Read
    MachineRentalResponse getMachineRental(Long id);
    Page<MachineRentalResponse> getAllMachineRentals(Pageable pageable);
    Page<MachineRentalResponse> getMachineRentalsByOwner(Long ownerId, Pageable pageable);
    Page<MachineRentalResponse> getMachineRentalsByType(MachineType machineType, Pageable pageable);
    Page<MachineRentalResponse> getAvailableMachineRentals(Pageable pageable);
    Page<MachineRentalResponse> searchMachineRentals(String keyword, String location, Pageable pageable);
    Page<MachineRentalResponse> advancedSearch(MachineType machineType, MachineStatus status,
                                               Double minPrice, Double maxPrice, String location, Pageable pageable);

    // Update
    MachineRentalResponse updateMachineRental(Long id, MachineRentalRequest request, List<MultipartFile> images, String username);
    MachineRentalResponse toggleAvailability(Long id, String username);
    MachineRentalResponse setPrimaryImage(Long imageId, String username);

    // Delete
    void deleteMachineRental(Long id, String username);
    void deleteImage(Long imageId, String username);
}