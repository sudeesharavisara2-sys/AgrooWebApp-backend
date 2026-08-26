package com.agroo.agroo.repository;

import com.agroo.agroo.model.Price;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    List<Price> findByProductNameAndLocationOrderByPriceDateDesc(String productName, String location);

    @Query("SELECT p FROM Price p WHERE p.priceDate = (SELECT MAX(p2.priceDate) FROM Price p2 WHERE p2.productName = p.productName AND p2.location = p.location)")
    List<Price> findLatestPrices();

    List<Price> findByPriceDate(LocalDate date);

    List<Price> findByProductNameContainingIgnoreCase(String productName);
}