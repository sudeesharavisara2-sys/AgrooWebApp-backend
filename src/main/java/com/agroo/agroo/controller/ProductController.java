package com.agroo.agroo.controller;

import com.agroo.agroo.dto.request.ProductRequest;
import com.agroo.agroo.dto.response.ProductResponse;
import com.agroo.agroo.model.enums.ProductCategory;
import com.agroo.agroo.model.enums.ProductType;
import com.agroo.agroo.model.enums.SaleType;
import com.agroo.agroo.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ============================================================
    // CREATE - Add new product listing (with images - multipart)
    // ============================================================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestPart("product") ProductRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) {
        String username = authentication.getName();
        ProductResponse response = productService.createProduct(request, username, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // CREATE - Add new product listing (JSON only - no images)
    // ============================================================
    @PostMapping("/json")
    public ResponseEntity<ProductResponse> createProductJson(
            @Valid @RequestBody ProductRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        ProductResponse response = productService.createProduct(request, username, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // READ - Get all products (with pagination)
    // ============================================================
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    // ============================================================
    // READ - Get product by ID
    // ============================================================
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    // ============================================================
    // READ - Get products by farmer
    // ============================================================
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByFarmer(
            @PathVariable Long farmerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(productService.getProductsByFarmer(farmerId, pageable));
    }

    // ============================================================
    // READ - Get products by category
    // ============================================================
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(productService.getProductsByCategory(category, pageable));
    }

    // ============================================================
    // READ - Get products by product type
    // ============================================================
    @GetMapping("/type/{productType}")
    public ResponseEntity<Page<ProductResponse>> getProductsByType(
            @PathVariable ProductType productType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(productService.getProductsByType(productType, pageable));
    }

    // ============================================================
    // READ - Get products by sale type
    // ============================================================
    @GetMapping("/sale-type/{saleType}")
    public ResponseEntity<Page<ProductResponse>> getProductsBySaleType(
            @PathVariable SaleType saleType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(productService.getProductsBySaleType(saleType, pageable));
    }

    // ============================================================
    // READ - Search products
    // ============================================================
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location) {
        return ResponseEntity.ok(productService.searchProducts(keyword, location));
    }

    // ============================================================
    // READ - Advanced search (Fixed - accepts String parameters)
    // ============================================================
    @GetMapping("/advanced-search")
    public ResponseEntity<List<ProductResponse>> advancedSearch(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) String saleType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String location) {
        return ResponseEntity.ok(productService.advancedSearch(category, productType, saleType, minPrice, maxPrice, location));
    }

    // ============================================================
    // UPDATE - Update product
    // ============================================================
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(productService.updateProduct(id, request, username));
    }

    // ============================================================
    // UPDATE - Toggle product availability
    // ============================================================
    @PatchMapping("/{id}/toggle-availability")
    public ResponseEntity<ProductResponse> toggleAvailability(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(productService.toggleAvailability(id, username));
    }

    // ============================================================
    // UPDATE - Set primary image
    // ============================================================
    @PatchMapping("/images/{imageId}/set-primary")
    public ResponseEntity<ProductResponse> setPrimaryImage(
            @PathVariable Long imageId,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(productService.setPrimaryImage(imageId, username));
    }

    // ============================================================
    // DELETE - Delete product
    // ============================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        productService.deleteProduct(id, username);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // DELETE - Delete product image
    // ============================================================
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long imageId,
            Authentication authentication) {
        String username = authentication.getName();
        productService.deleteImage(imageId, username);
        return ResponseEntity.noContent().build();
    }
}