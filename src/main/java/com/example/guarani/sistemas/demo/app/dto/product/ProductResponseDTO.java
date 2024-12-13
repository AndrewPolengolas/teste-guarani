package com.example.guarani.sistemas.demo.app.dto.product;

import com.example.guarani.sistemas.demo.domain.enums.Category;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        Category category
) {}