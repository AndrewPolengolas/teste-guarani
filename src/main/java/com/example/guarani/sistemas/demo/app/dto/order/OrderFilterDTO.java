package com.example.guarani.sistemas.demo.app.dto.order;

import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderFilterDTO(
        OrderStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal minAmount,
        BigDecimal maxAmount
) {}