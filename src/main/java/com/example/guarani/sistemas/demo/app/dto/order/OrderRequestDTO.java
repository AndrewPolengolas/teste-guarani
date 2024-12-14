package com.example.guarani.sistemas.demo.app.dto.order;

import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequestDTO(
        Long customerId,
        List<Long> productIds,
        @DecimalMin(value = "0.0", inclusive = true, message = "Discount must be at least 0")
        @DecimalMax(value = "1.0", inclusive = true, message = "Discount must be at most 1.0")
        BigDecimal discount,
        BigDecimal shippingFee
) {}