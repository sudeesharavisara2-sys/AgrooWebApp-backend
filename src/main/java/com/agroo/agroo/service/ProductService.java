package com.agroo.agroo.service;

import com.agroo.agroo.dto.request.ProductRequest;
import com.agroo.agroo.dto.response.ProductResponse;
import com.agroo.agroo.model.enums.ProductCategory;
import com.agroo.agroo.model.enums.ProductType;
import com.agroo.agroo.model.enums.SaleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    // Create
    ProductResponse createProduct(ProductRequest request, String username, List<MultipartFile> images);

    // Read
    ProductResponse getProduct(Long productId);
    Page<ProductResponse> getAllProducts(Pageable pageable);
    Page<ProductResponse> getProductsByFarmer(Long farmerId, Pageable pageable);
    Page<ProductResponse> getProductsByCategory(ProductCategory category, Pageable pageable);
    Page<ProductResponse> getProductsByType(ProductType productType, Pageable pageable);
    Page<ProductResponse> getProductsBySaleType(SaleType saleType, Pageable pageable);
    List<ProductResponse> searchProducts(String keyword, String location);
    List<ProductResponse> advancedSearch(String category, String productType, String saleType,
                                         Double minPrice, Double maxPrice, String location);

    // Update
    ProductResponse updateProduct(Long productId, ProductRequest request, String username);
    ProductResponse toggleAvailability(Long productId, String username);
    ProductResponse setPrimaryImage(Long imageId, String username);

    // Delete
    void deleteProduct(Long productId, String username);
    void deleteImage(Long imageId, String username);
}