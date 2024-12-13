package com.example.guarani.sistemas.demo.app.mapper;

import com.example.guarani.sistemas.demo.app.dto.payment.PaymentRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.payment.PaymentResponseDTO;
import com.example.guarani.sistemas.demo.domain.model.Order;
import com.example.guarani.sistemas.demo.domain.model.Payment;
import com.example.guarani.sistemas.demo.domain.repository.OrderRepository;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    private final OrderRepository orderRepository;

    public PaymentMapper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Payment toPayment(PaymentRequestDTO paymentRequestDTO) {
        Order order = orderRepository.findById(paymentRequestDTO.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + paymentRequestDTO.orderId()));

        return new Payment(
                null,
                order,
                paymentRequestDTO.amount(),
                paymentRequestDTO.method(),
                paymentRequestDTO.transactionId(),
                paymentRequestDTO.paymentDate(),
                paymentRequestDTO.status()
        );
    }

    public PaymentResponseDTO toPaymentResponseDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getTransactionId(),
                payment.getPaymentDate(),
                payment.getStatus()
        );
    }
}