package com.example.guarani.sistemas.demo.app.dto.order;

import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.Date;

public record OrderFilterDTO(
        OrderStatus status,
        Date startDate,
        Date endDate,
        BigDecimal minAmount,
        BigDecimal maxAmount
) {}