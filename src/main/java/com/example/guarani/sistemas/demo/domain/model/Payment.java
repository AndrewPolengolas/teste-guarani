package com.example.guarani.sistemas.demo.domain.model;

import com.example.guarani.sistemas.demo.domain.enums.PaymentMethod;
import com.example.guarani.sistemas.demo.domain.enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private BigDecimal amount;
    private PaymentMethod method; // Enum: CREDIT_CARD, BOLETO, BANK_TRANSFER
    private String transactionId;
    private LocalDateTime paymentDate;
    private PaymentStatus status; // Enum: PENDING, COMPLETED, FAILED

    public Payment() {}

    public Payment(Long id, Order order, BigDecimal amount, PaymentMethod method, String transactionId, LocalDateTime paymentDate, PaymentStatus status) {
        this.id = id;
        this.order = order;
        this.amount = amount;
        this.method = method;
        this.transactionId = transactionId;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
