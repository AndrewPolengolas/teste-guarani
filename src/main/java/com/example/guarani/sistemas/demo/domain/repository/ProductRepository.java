package com.example.guarani.sistemas.demo.domain.repository;

import com.example.guarani.sistemas.demo.domain.enums.Category;
import com.example.guarani.sistemas.demo.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT * FROM product p " +
            " WHERE 1 = 1 " +
            "   AND (p.price >= :minPrice AND p.price <= :maxPrice OR(:minPrice IS NULL AND :maxPrice IS NULL)) " +
            "   AND (p.category LIKE :category OR :category IS NULL) " +
            " ORDER BY p.price ASC", nativeQuery = true)
    List<Product> findByFilters(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("category") String category
    );
}
