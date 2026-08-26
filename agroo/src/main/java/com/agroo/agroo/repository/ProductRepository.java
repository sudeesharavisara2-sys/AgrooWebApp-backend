package com.agroo.agroo.repository;

import com.agroo.agroo.model.Product;
import com.agroo.agroo.model.enums.ProductCategory;
import com.agroo.agroo.model.enums.ProductType;
import com.agroo.agroo.model.enums.SaleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ============================================================
    // FIND BY FARMER
    // ============================================================
    List<Product> findByFarmerId(Long farmerId);
    Page<Product> findByFarmerId(Long farmerId, Pageable pageable);

    // ============================================================
    // FIND BY CATEGORY
    // ============================================================
    List<Product> findByCategory(ProductCategory category);
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);

    // ============================================================
    // FIND BY PRODUCT TYPE
    // ============================================================
    List<Product> findByProductType(ProductType productType);
    Page<Product> findByProductType(ProductType productType, Pageable pageable);

    // ============================================================
    // FIND BY SALE TYPE
    // ============================================================
    List<Product> findBySaleType(SaleType saleType);
    Page<Product> findBySaleType(SaleType saleType, Pageable pageable);

    // ============================================================
    // FIND AVAILABLE PRODUCTS
    // ============================================================
    List<Product> findByIsAvailableTrue();
    Page<Product> findByIsAvailableTrue(Pageable pageable);

    // ============================================================
    // FIND BY CATEGORY AND AVAILABLE
    // ============================================================
    List<Product> findByCategoryAndIsAvailableTrue(ProductCategory category);
    Page<Product> findByCategoryAndIsAvailableTrue(ProductCategory category, Pageable pageable);

    // ============================================================
    // COUNT METHODS
    // ============================================================
    Long countByFarmerId(Long farmerId);
    Long countByIsAvailableTrue();
    Long countByCategory(ProductCategory category);

    // ============================================================
    // SEARCH BY NAME
    // ============================================================
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByName(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByName(@Param("keyword") String keyword, Pageable pageable);

    // ============================================================
    // SEARCH BY LOCATION (Fixed for PostgreSQL)
    // ============================================================
    @Query(value = "SELECT * FROM products p WHERE LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))", nativeQuery = true)
    List<Product> searchByLocation(@Param("location") String location);

    @Query(value = "SELECT * FROM products p WHERE LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))",
            countQuery = "SELECT COUNT(*) FROM products p WHERE LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))",
            nativeQuery = true)
    Page<Product> searchByLocation(@Param("location") String location, Pageable pageable);

    // ============================================================
    // ADVANCED SEARCH (Native Query for PostgreSQL)
    // ============================================================
    @Query(value = "SELECT * FROM products p WHERE " +
            "(:category IS NULL OR p.category = CAST(:category AS VARCHAR)) AND " +
            "(:productType IS NULL OR p.product_type = CAST(:productType AS VARCHAR)) AND " +
            "(:saleType IS NULL OR p.sale_type = CAST(:saleType AS VARCHAR)) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "p.is_available = true",
            nativeQuery = true)
    List<Product> advancedSearch(
            @Param("category") String category,
            @Param("productType") String productType,
            @Param("saleType") String saleType,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("location") String location
    );

    // ============================================================
    // ALTERNATIVE ADVANCED SEARCH (JPQL - Works with Hibernate)
    // ============================================================
    @Query("SELECT p FROM Product p WHERE " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:productType IS NULL OR p.productType = :productType) AND " +
            "(:saleType IS NULL OR p.saleType = :saleType) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:location IS NULL OR LOWER(CAST(p.location AS string)) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "p.isAvailable = true")
    List<Product> advancedSearchJPQL(
            @Param("category") ProductCategory category,
            @Param("productType") ProductType productType,
            @Param("saleType") SaleType saleType,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("location") String location
    );

    // ============================================================
    // RECENT PRODUCTS
    // ============================================================
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findRecentProducts(Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    Page<Product> findRecentProductsPage(Pageable pageable);

    // ============================================================
    // FIND BY CATEGORY WITH JOIN FETCH (Fix lazy loading)
    // ============================================================
    @Query("SELECT p FROM Product p JOIN FETCH p.farmer WHERE p.id = :id")
    Product findByIdWithFarmer(@Param("id") Long id);

    @Query("SELECT p FROM Product p JOIN FETCH p.farmer")
    List<Product> findAllWithFarmer();

    @Query("SELECT p FROM Product p JOIN FETCH p.farmer WHERE p.category = :category")
    List<Product> findByCategoryWithFarmer(@Param("category") ProductCategory category);

    // ============================================================
    // STATS & ANALYTICS
    // ============================================================
    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
    List<Object[]> countByCategory();

    @Query("SELECT p.productType, COUNT(p) FROM Product p GROUP BY p.productType")
    List<Object[]> countByProductType();

    @Query("SELECT p.saleType, COUNT(p) FROM Product p GROUP BY p.saleType")
    List<Object[]> countBySaleType();

    @Query("SELECT p.category, AVG(p.price) FROM Product p GROUP BY p.category")
    List<Object[]> averagePriceByCategory();

    // ============================================================
    // FIND BY PRICE RANGE
    // ============================================================
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    // ============================================================
    // FIND BY LOCATION AND CATEGORY
    // ============================================================
    List<Product> findByLocationContainingIgnoreCaseAndCategory(String location, ProductCategory category);
    Page<Product> findByLocationContainingIgnoreCaseAndCategory(String location, ProductCategory category, Pageable pageable);

    // ============================================================
    // FIND AVAILABLE PRODUCTS BY CATEGORY
    // ============================================================
    List<Product> findByIsAvailableTrueAndCategory(ProductCategory category);
    Page<Product> findByIsAvailableTrueAndCategory(ProductCategory category, Pageable pageable);

    // ============================================================
    // FIND PRODUCTS CREATED AFTER DATE
    // ============================================================
    @Query("SELECT p FROM Product p WHERE p.createdAt >= :date")
    List<Product> findProductsCreatedAfter(@Param("date") java.time.LocalDateTime date);

    @Query("SELECT p FROM Product p WHERE p.createdAt >= :date ORDER BY p.createdAt DESC")
    Page<Product> findRecentProductsSince(@Param("date") java.time.LocalDateTime date, Pageable pageable);

    // ============================================================
    // FIND TOP RATED / MOST VIEWED
    // ============================================================
    @Query("SELECT p FROM Product p ORDER BY p.viewCount DESC")
    List<Product> findMostViewedProducts(Pageable pageable);

    // ============================================================
    // FIND BY ORGANIC STATUS
    // ============================================================
    List<Product> findByIsOrganicTrue();
    Page<Product> findByIsOrganicTrue(Pageable pageable);

    List<Product> findByIsOrganicTrueAndIsAvailableTrue();
    Page<Product> findByIsOrganicTrueAndIsAvailableTrue(Pageable pageable);

    // ============================================================
    // SEARCH WITH MULTIPLE CRITERIA (JPQL)
    // ============================================================
    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:productType IS NULL OR p.productType = :productType) AND " +
            "(:saleType IS NULL OR p.saleType = :saleType) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:availableOnly IS NULL OR p.isAvailable = true)")
    List<Product> searchWithFilters(
            @Param("keyword") String keyword,
            @Param("category") ProductCategory category,
            @Param("productType") ProductType productType,
            @Param("saleType") SaleType saleType,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("location") String location,
            @Param("availableOnly") Boolean availableOnly
    );

    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:productType IS NULL OR p.productType = :productType) AND " +
            "(:saleType IS NULL OR p.saleType = :saleType) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:availableOnly IS NULL OR p.isAvailable = true)")
    Page<Product> searchWithFiltersPage(
            @Param("keyword") String keyword,
            @Param("category") ProductCategory category,
            @Param("productType") ProductType productType,
            @Param("saleType") SaleType saleType,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("location") String location,
            @Param("availableOnly") Boolean availableOnly,
            Pageable pageable
    );
}