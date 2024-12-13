package com.example.guarani.sistemas.demo.app.dto.order;

import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequestDTO(
        Long customerId,
        List<Long> productIds,
        BigDecimal discount,
        BigDecimal shippingFee,
        OrderStatus status
) {}