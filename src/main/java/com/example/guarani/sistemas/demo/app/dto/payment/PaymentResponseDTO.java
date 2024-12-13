package com.example.guarani.sistemas.demo.app.dto.payment;

import com.example.guarani.sistemas.demo.domain.enums.PaymentMethod;
import com.example.guarani.sistemas.demo.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDTO(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentMethod method,
        String transactionId,
        LocalDateTime paymentDate,
        PaymentStatus status
) {}