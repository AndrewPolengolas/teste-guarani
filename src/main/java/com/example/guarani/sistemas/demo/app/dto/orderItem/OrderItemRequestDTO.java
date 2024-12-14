package com.example.guarani.sistemas.demo.app.dto.orderItem;

import java.math.BigDecimal;

public record OrderItemRequestDTO(
        Long productId,
        int quantity
) { }