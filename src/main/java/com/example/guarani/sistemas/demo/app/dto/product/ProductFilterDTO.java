package com.example.guarani.sistemas.demo.app.dto.product;

import com.example.guarani.sistemas.demo.domain.enums.Category;

import java.math.BigDecimal;

public record ProductFilterDTO(
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Category category
){}
