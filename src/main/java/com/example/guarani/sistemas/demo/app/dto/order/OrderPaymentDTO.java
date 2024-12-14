package com.example.guarani.sistemas.demo.app.dto.order;

import com.example.guarani.sistemas.demo.domain.enums.PaymentType;

public record OrderPaymentDTO(
    String paymentType
) {
}
