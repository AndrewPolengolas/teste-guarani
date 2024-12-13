package com.example.guarani.sistemas.demo.domain.repository;

import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;
import com.example.guarani.sistemas.demo.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:startDate IS NULL OR o.creationDate >= :startDate) AND " +
            "(:endDate IS NULL OR o.creationDate <= :endDate) AND " +
            "(:minAmount IS NULL OR o.totalAmount >= :minAmount) AND " +
            "(:maxAmount IS NULL OR o.totalAmount <= :maxAmount)")
    List<Order> findByFilters(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount
    );
}