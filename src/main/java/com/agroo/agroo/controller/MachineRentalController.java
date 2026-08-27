package com.agroo.agroo.controller;

import com.agroo.agroo.dto.request.MachineRentalRequest;
import com.agroo.agroo.dto.response.MachineRentalResponse;
import com.agroo.agroo.model.enums.MachineStatus;
import com.agroo.agroo.model.enums.MachineType;
import com.agroo.agroo.service.MachineRentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/machines")
@RequiredArgsConstructor
public class MachineRentalController {

    private final MachineRentalService machineRentalService;

    // ============================================================
    // CREATE - Add new machine rental listing
    // ============================================================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MachineRentalResponse> createMachineRental(
            @Valid @RequestPart("machine") MachineRentalRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(machineRentalService.createMachineRental(request, images, username));
    }

    // ============================================================
    // READ - Get all machine rentals (Public)
    // ============================================================
    @GetMapping
    public ResponseEntity<Page<MachineRentalResponse>> getAllMachineRentals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(machineRentalService.getAllMachineRentals(pageable));
    }

    // ============================================================
    // READ - Get machine rental by ID (Public)
    // ============================================================
    @GetMapping("/{id}")
    public ResponseEntity<MachineRentalResponse> getMachineRental(@PathVariable Long id) {
        return ResponseEntity.ok(machineRentalService.getMachineRental(id));
    }

    // ============================================================
    // READ - Get machines by owner (Public)
    // ============================================================
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<MachineRentalResponse>> getMachinesByOwner(
            @PathVariable Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(machineRentalService.getMachineRentalsByOwner(ownerId, pageable));
    }

    // ============================================================
    // READ - Get machines by type (Public)
    // ============================================================
    @GetMapping("/type/{machineType}")
    public ResponseEntity<Page<MachineRentalResponse>> getMachinesByType(
            @PathVariable MachineType machineType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(machineRentalService.getMachineRentalsByType(machineType, pageable));
    }

    // ============================================================
    // READ - Get available machines (Public)
    // ============================================================
    @GetMapping("/available")
    public ResponseEntity<Page<MachineRentalResponse>> getAvailableMachines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(machineRentalService.getAvailableMachineRentals(pageable));
    }

    // ============================================================
    // READ - Search machines (Public)
    // ============================================================
    @GetMapping("/search")
    public ResponseEntity<Page<MachineRentalResponse>> searchMachines(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(machineRentalService.searchMachineRentals(keyword, location, pageable));
    }

    // ============================================================
    // READ - Advanced search (Public)
    // ============================================================
    @GetMapping("/advanced-search")
    public ResponseEntity<Page<MachineRentalResponse>> advancedSearch(
            @RequestParam(required = false) MachineType machineType,
            @RequestParam(required = false) MachineStatus status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(machineRentalService.advancedSearch(
                machineType, status, minPrice, maxPrice, location, pageable));
    }

    // ============================================================
    // UPDATE - Update machine rental (Authenticated - Owner only)
    // ============================================================
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MachineRentalResponse> updateMachineRental(
            @PathVariable Long id,
            @Valid @RequestPart("machine") MachineRentalRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(machineRentalService.updateMachineRental(id, request, images, username));
    }

    // ============================================================
    // UPDATE - Toggle availability (Authenticated - Owner only)
    // ============================================================
    @PatchMapping("/{id}/toggle-availability")
    public ResponseEntity<MachineRentalResponse> toggleAvailability(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(machineRentalService.toggleAvailability(id, username));
    }

    // ============================================================
    // UPDATE - Set primary image (Authenticated - Owner only)
    // ============================================================
    @PatchMapping("/images/{imageId}/set-primary")
    public ResponseEntity<MachineRentalResponse> setPrimaryImage(
            @PathVariable Long imageId,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(machineRentalService.setPrimaryImage(imageId, username));
    }

    // ============================================================
    // DELETE - Delete machine rental (Authenticated - Owner only)
    // ============================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMachineRental(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        machineRentalService.deleteMachineRental(id, username);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // DELETE - Delete machine image (Authenticated - Owner only)
    // ============================================================
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long imageId,
            Authentication authentication) {
        String username = authentication.getName();
        machineRentalService.deleteImage(imageId, username);
        return ResponseEntity.noContent().build();
    }
}