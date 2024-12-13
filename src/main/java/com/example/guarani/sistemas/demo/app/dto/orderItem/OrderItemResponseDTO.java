package com.example.guarani.sistemas.demo.app.dto.orderItem;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long id,
        Long productId,
        String productName,
        int quantity,
        BigDecimal itemPrice,
        BigDecimal totalPrice
) {}