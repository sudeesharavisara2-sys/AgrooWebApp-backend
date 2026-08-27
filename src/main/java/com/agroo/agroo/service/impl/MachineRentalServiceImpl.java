package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.request.MachineRentalRequest;
import com.agroo.agroo.dto.response.MachineRentalResponse;
import com.agroo.agroo.model.MachineImage;
import com.agroo.agroo.model.MachineRental;
import com.agroo.agroo.model.User;
import com.agroo.agroo.model.enums.MachineStatus;
import com.agroo.agroo.model.enums.MachineType;
import com.agroo.agroo.repository.MachineRentalRepository;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.FileStorageService;
import com.agroo.agroo.service.MachineRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MachineRentalServiceImpl implements MachineRentalService {

    private final MachineRentalRepository machineRentalRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    // CREATE
    @Override
    @Transactional
    public MachineRentalResponse createMachineRental(MachineRentalRequest request,
                                                     List<MultipartFile> images,
                                                     String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MachineRental machine = new MachineRental();
        mapRequestToMachine(request, machine);
        machine.setOwner(owner);

        if (images != null && !images.isEmpty()) {
            List<MachineImage> machineImages = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                String imageUrl = fileStorageService.storeFile(file);
                MachineImage machineImage = new MachineImage();
                machineImage.setImageUrl(imageUrl);
                machineImage.setFileName(file.getOriginalFilename());
                machineImage.setFileType(file.getContentType());
                machineImage.setFileSize(file.getSize());
                machineImage.setDisplayOrder(i);
                machineImage.setIsPrimary(i == 0);
                machineImage.setMachineRental(machine);
                machineImages.add(machineImage);
            }
            machine.setImages(machineImages);
        }

        machine = machineRentalRepository.save(machine);
        return mapToResponse(machine);
    }

    // READ
    @Override
    @Transactional(readOnly = true)
    public MachineRentalResponse getMachineRental(Long id) {
        MachineRental machine = machineRentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine rental not found"));

        machine.setViewCount(machine.getViewCount() + 1);
        machineRentalRepository.save(machine);

        return mapToResponse(machine);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineRentalResponse> getAllMachineRentals(Pageable pageable) {
        return machineRentalRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineRentalResponse> getMachineRentalsByOwner(Long ownerId, Pageable pageable) {
        return machineRentalRepository.findByOwnerId(ownerId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineRentalResponse> getMachineRentalsByType(MachineType machineType, Pageable pageable) {
        return machineRentalRepository.findByMachineType(machineType, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineRentalResponse> getAvailableMachineRentals(Pageable pageable) {
        return machineRentalRepository.findByIsAvailableTrue(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineRentalResponse> searchMachineRentals(String keyword, String location, Pageable pageable) {
        Page<MachineRental> page;
        if (keyword != null && !keyword.isEmpty()) {
            page = machineRentalRepository.searchByName(keyword, pageable);
        } else if (location != null && !location.isEmpty()) {
            page = machineRentalRepository.searchByLocation(location, pageable);
        } else {
            page = machineRentalRepository.findAll(pageable);
        }
        return page.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineRentalResponse> advancedSearch(MachineType machineType, MachineStatus status,
                                                      Double minPrice, Double maxPrice,
                                                      String location, Pageable pageable) {
        return machineRentalRepository.advancedSearch(machineType, status, minPrice, maxPrice, location, pageable)
                .map(this::mapToResponse);
    }

    // UPDATE
    @Override
    @Transactional
    public MachineRentalResponse updateMachineRental(Long id, MachineRentalRequest request,
                                                     List<MultipartFile> images, String username) {
        MachineRental machine = machineRentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine rental not found"));

        if (!machine.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to update this listing");
        }

        mapRequestToMachine(request, machine);

        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                String imageUrl = fileStorageService.storeFile(file);
                MachineImage machineImage = new MachineImage();
                machineImage.setImageUrl(imageUrl);
                machineImage.setFileName(file.getOriginalFilename());
                machineImage.setFileType(file.getContentType());
                machineImage.setFileSize(file.getSize());
                machineImage.setDisplayOrder(machine.getImages().size() + i);
                machineImage.setIsPrimary(machine.getImages().isEmpty() && i == 0);
                machineImage.setMachineRental(machine);
                machine.addImage(machineImage);
            }
        }

        machine = machineRentalRepository.save(machine);
        return mapToResponse(machine);
    }

    @Override
    @Transactional
    public MachineRentalResponse toggleAvailability(Long id, String username) {
        MachineRental machine = machineRentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine rental not found"));

        if (!machine.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to modify this listing");
        }

        machine.setIsAvailable(!machine.getIsAvailable());
        machine.setStatus(machine.getIsAvailable() ? MachineStatus.AVAILABLE : MachineStatus.NOT_AVAILABLE);
        machine = machineRentalRepository.save(machine);
        return mapToResponse(machine);
    }

    @Override
    @Transactional
    public MachineRentalResponse setPrimaryImage(Long imageId, String username) {
        MachineImage image = findImageById(imageId);
        MachineRental machine = image.getMachineRental();

        if (!machine.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to modify this listing");
        }

        for (MachineImage img : machine.getImages()) {
            img.setIsPrimary(false);
        }
        image.setIsPrimary(true);
        return mapToResponse(machine);
    }

    // DELETE
    @Override
    @Transactional
    public void deleteMachineRental(Long id, String username) {
        MachineRental machine = machineRentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine rental not found"));

        if (!machine.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to delete this listing");
        }

        for (MachineImage image : machine.getImages()) {
            fileStorageService.deleteFile(image.getImageUrl());
        }

        machineRentalRepository.delete(machine);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId, String username) {
        MachineImage image = findImageById(imageId);
        MachineRental machine = image.getMachineRental();

        if (!machine.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to delete this image");
        }

        fileStorageService.deleteFile(image.getImageUrl());
        machine.getImages().remove(image);

        if (image.getIsPrimary() && !machine.getImages().isEmpty()) {
            machine.getImages().get(0).setIsPrimary(true);
        }
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================
    private void mapRequestToMachine(MachineRentalRequest request, MachineRental machine) {
        machine.setName(request.getName());
        machine.setDescription(request.getDescription());
        machine.setMachineType(request.getMachineType());
        machine.setPricePerDay(request.getPricePerDay());
        machine.setPricePerHour(request.getPricePerHour());
        machine.setPricePerAcre(request.getPricePerAcre());
        machine.setLocation(request.getLocation());
        machine.setDistrict(request.getDistrict());
        machine.setContactPhone(request.getContactPhone());
        machine.setContactWhatsapp(request.getContactWhatsapp());
        machine.setStatus(request.getStatus() != null ? request.getStatus() : MachineStatus.AVAILABLE);
        machine.setIsAvailable(request.getStatus() == null || request.getStatus() == MachineStatus.AVAILABLE);
        machine.setYearOfManufacture(request.getYearOfManufacture());
        machine.setBrand(request.getBrand());
        machine.setModel(request.getModel());
        machine.setFuelType(request.getFuelType());
        machine.setHorsePower(request.getHorsePower());

        if (request.getFeatures() != null) {
            machine.setFeatures(new ArrayList<>(request.getFeatures()));
        } else {
            machine.setFeatures(new ArrayList<>());
        }
    }

    private MachineRentalResponse mapToResponse(MachineRental machine) {
        User owner = machine.getOwner();

        return MachineRentalResponse.builder()
                .id(machine.getId())
                .name(machine.getName())
                .description(machine.getDescription())
                .machineType(machine.getMachineType())
                .pricePerDay(machine.getPricePerDay())
                .pricePerHour(machine.getPricePerHour())
                .pricePerAcre(machine.getPricePerAcre())
                .location(machine.getLocation())
                .district(machine.getDistrict())
                .contactPhone(machine.getContactPhone())
                .contactWhatsapp(machine.getContactWhatsapp())
                .isAvailable(machine.getIsAvailable())
                .status(machine.getStatus())
                .isVerified(machine.getIsVerified())
                .viewCount(machine.getViewCount())
                .yearOfManufacture(machine.getYearOfManufacture())
                .brand(machine.getBrand())
                .model(machine.getModel())
                .fuelType(machine.getFuelType())
                .horsePower(machine.getHorsePower())
                .features(machine.getFeatures() != null ? new ArrayList<>(machine.getFeatures()) : new ArrayList<>())
                .owner(MachineRentalResponse.OwnerInfo.builder()
                        .id(owner.getId())
                        .username(owner.getUsername())
                        .fullName(owner.getFullName())
                        .phoneNumber(owner.getPhoneNumber())
                        .profileImageUrl(owner.getProfileImageUrl())
                        .build())
                .images(machine.getImages().stream()
                        .map(img -> MachineRentalResponse.MachineImageInfo.builder()
                                .id(img.getId())
                                .imageUrl(img.getImageUrl())
                                .isPrimary(img.getIsPrimary())
                                .displayOrder(img.getDisplayOrder())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(machine.getCreatedAt())
                .updatedAt(machine.getUpdatedAt())
                .build();
    }

    private MachineImage findImageById(Long imageId) {
        return machineRentalRepository.findAll().stream()
                .flatMap(m -> m.getImages().stream())
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }
}