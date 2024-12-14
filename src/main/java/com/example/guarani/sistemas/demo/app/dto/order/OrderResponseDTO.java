package com.example.guarani.sistemas.demo.app.dto.order;

import com.example.guarani.sistemas.demo.app.dto.orderItem.OrderItemResponseDTO;
import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        Long customerId,
        List<OrderItemResponseDTO> items,
        BigDecimal totalAmount,
        BigDecimal discount,
        BigDecimal shippingFee,
        OrderStatus status,
        Date creationDate
) {}