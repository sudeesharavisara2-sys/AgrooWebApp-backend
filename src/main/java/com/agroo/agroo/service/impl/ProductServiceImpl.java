package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.request.ProductRequest;
import com.agroo.agroo.dto.response.ProductResponse;
import com.agroo.agroo.model.Product;
import com.agroo.agroo.model.ProductImage;
import com.agroo.agroo.model.User;
import com.agroo.agroo.model.enums.ProductCategory;
import com.agroo.agroo.model.enums.ProductType;
import com.agroo.agroo.model.enums.SaleType;
import com.agroo.agroo.repository.ProductImageRepository;
import com.agroo.agroo.repository.ProductRepository;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.FileStorageService;
import com.agroo.agroo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    // ============================================================
    // CREATE
    // ============================================================
    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request, String username, List<MultipartFile> images) {
        User farmer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = new Product();
        mapRequestToProduct(request, product);
        product.setFarmer(farmer);

        // Handle images
        if (images != null && !images.isEmpty()) {
            List<ProductImage> productImages = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                String imageUrl = fileStorageService.storeFile(file);
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(imageUrl);
                productImage.setFileName(file.getOriginalFilename());
                productImage.setFileType(file.getContentType());
                productImage.setFileSize(file.getSize());
                productImage.setDisplayOrder(i);
                productImage.setIsPrimary(i == 0);
                productImage.setProduct(product);
                productImages.add(productImage);
            }
            product.setImages(productImages);
        }

        product = productRepository.save(product);
        return mapToResponse(product);
    }

    // ============================================================
    // READ
    // ============================================================
    @Override
    @Transactional
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Increment view count
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByFarmer(Long farmerId, Pageable pageable) {
        return productRepository.findByFarmerId(farmerId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByType(ProductType productType, Pageable pageable) {
        return productRepository.findByProductType(productType, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsBySaleType(SaleType saleType, Pageable pageable) {
        return productRepository.findBySaleType(saleType, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword, String location) {
        List<Product> products = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            products.addAll(productRepository.searchByName(keyword));
        }
        if (location != null && !location.isEmpty()) {
            products.addAll(productRepository.searchByLocation(location));
        }
        return products.stream()
                .distinct()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> advancedSearch(String category, String productType, String saleType,
                                                Double minPrice, Double maxPrice, String location) {
        return productRepository.advancedSearch(category, productType, saleType, minPrice, maxPrice, location)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ============================================================
    // UPDATE
    // ============================================================
    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request, String username) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Verify ownership
        if (!product.getFarmer().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to update this product");
        }

        mapRequestToProduct(request, product);
        product = productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse toggleAvailability(Long productId, String username) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getFarmer().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to modify this product");
        }

        product.setIsAvailable(!product.getIsAvailable());
        product = productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse setPrimaryImage(Long imageId, String username) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Product product = image.getProduct();
        if (!product.getFarmer().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to modify this product");
        }

        // Set all images to not primary
        for (ProductImage img : product.getImages()) {
            img.setIsPrimary(false);
            productImageRepository.save(img);
        }

        // Set selected image as primary
        image.setIsPrimary(true);
        productImageRepository.save(image);

        return mapToResponse(product);
    }

    // ============================================================
    // DELETE
    // ============================================================
    @Override
    @Transactional
    public void deleteProduct(Long productId, String username) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getFarmer().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to delete this product");
        }

        // Delete images from storage
        for (ProductImage image : product.getImages()) {
            fileStorageService.deleteFile(image.getImageUrl());
        }

        productRepository.delete(product);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId, String username) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Product product = image.getProduct();
        if (!product.getFarmer().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to delete this image");
        }

        fileStorageService.deleteFile(image.getImageUrl());
        productImageRepository.delete(image);

        // If deleted image was primary, set another as primary
        if (image.getIsPrimary() && !product.getImages().isEmpty()) {
            ProductImage firstImage = product.getImages().get(0);
            firstImage.setIsPrimary(true);
            productImageRepository.save(firstImage);
        }
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================
    private void mapRequestToProduct(ProductRequest request, Product product) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setUnit(request.getUnit());
        product.setCategory(request.getCategory());
        product.setProductType(request.getProductType());
        product.setSaleType(request.getSaleType());
        product.setLocation(request.getLocation());
        product.setDistrict(request.getDistrict());
        product.setAddress(request.getAddress());
        product.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
        product.setIsOrganic(request.getIsOrganic() != null ? request.getIsOrganic() : false);
        product.setContactPhone(request.getContactPhone());
        product.setContactWhatsapp(request.getContactWhatsapp());

        if (request.getHarvestDate() != null && !request.getHarvestDate().isEmpty()) {
            product.setHarvestDate(LocalDateTime.parse(request.getHarvestDate(), DateTimeFormatter.ISO_DATE_TIME));
        }
        if (request.getExpiryDate() != null && !request.getExpiryDate().isEmpty()) {
            product.setExpiryDate(LocalDateTime.parse(request.getExpiryDate(), DateTimeFormatter.ISO_DATE_TIME));
        }
    }

    private ProductResponse mapToResponse(Product product) {
        // Force initialize the farmer proxy
        User farmer = product.getFarmer();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .unit(product.getUnit())
                .category(product.getCategory())
                .productType(product.getProductType())
                .saleType(product.getSaleType())
                .location(product.getLocation())
                .district(product.getDistrict())
                .address(product.getAddress())
                .isAvailable(product.getIsAvailable())
                .isOrganic(product.getIsOrganic())
                .isVerified(product.getIsVerified())
                .contactPhone(product.getContactPhone())
                .contactWhatsapp(product.getContactWhatsapp())
                .harvestDate(product.getHarvestDate())
                .expiryDate(product.getExpiryDate())
                .viewCount(product.getViewCount())
                .farmer(ProductResponse.UserInfo.builder()
                        .id(farmer.getId())
                        .username(farmer.getUsername())
                        .fullName(farmer.getFullName())
                        .phoneNumber(farmer.getPhoneNumber())
                        .profileImageUrl(farmer.getProfileImageUrl())
                        .build())
                .images(product.getImages().stream()
                        .map(img -> ProductResponse.ProductImageInfo.builder()
                                .id(img.getId())
                                .imageUrl(img.getImageUrl())
                                .isPrimary(img.getIsPrimary())
                                .displayOrder(img.getDisplayOrder())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}