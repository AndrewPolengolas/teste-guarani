package com.example.guarani.sistemas.demo.domain.repository;

import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;
import com.example.guarani.sistemas.demo.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT * FROM customer_order o " +
            " WHERE 1 = 1 " +
            " AND (o.status LIKE :status OR :status IS NULL) " +
            " AND (o.creation_date BETWEEN :startDate AND :endDate OR(:startDate IS NULL AND :endDate IS NULL)) " +
            " AND (o.total_amount BETWEEN :minAmount AND :maxAmount OR(:minAmount IS NULL AND :maxAmount IS NULL)) ", nativeQuery = true)
    List<Order> findByFilters(
            @Param("status") String status,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount
    );

}