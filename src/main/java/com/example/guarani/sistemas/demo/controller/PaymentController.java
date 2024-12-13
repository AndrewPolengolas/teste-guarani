package com.example.guarani.sistemas.demo.controller;

import com.example.guarani.sistemas.demo.app.dto.payment.PaymentRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.payment.PaymentResponseDTO;
import com.example.guarani.sistemas.demo.app.service.PaymentService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        PaymentResponseDTO dto = paymentService.createPayment(paymentRequestDTO);
        return ResponseEntity.status(201).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(
            @PathVariable Long id,
            @RequestBody PaymentRequestDTO paymentRequestDTO) {
        PaymentResponseDTO dto = paymentService.updatePayment(id, paymentRequestDTO);

        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO dto = paymentService.getPaymentById(id);

        return ResponseEntity.ok().body(dto);
    }
}