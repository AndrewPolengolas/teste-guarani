package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.mapper.PaymentMapper;
import com.example.guarani.sistemas.demo.app.dto.payment.PaymentRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.payment.PaymentResponseDTO;
import com.example.guarani.sistemas.demo.domain.model.Payment;
import com.example.guarani.sistemas.demo.domain.repository.PaymentRepository;
import com.example.guarani.sistemas.demo.infra.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO) {
        Payment payment = paymentMapper.toPayment(paymentRequestDTO);
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponseDTO(savedPayment);
    }

    @Transactional
    public PaymentResponseDTO updatePayment(Long id, PaymentRequestDTO paymentRequestDTO) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        payment.setAmount(paymentRequestDTO.amount());
        payment.setMethod(paymentRequestDTO.method());
        payment.setTransactionId(paymentRequestDTO.transactionId());
        payment.setPaymentDate(paymentRequestDTO.paymentDate());
        payment.setStatus(paymentRequestDTO.status());

        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponseDTO(updatedPayment);
    }

    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return paymentMapper.toPaymentResponseDTO(payment);
    }
}