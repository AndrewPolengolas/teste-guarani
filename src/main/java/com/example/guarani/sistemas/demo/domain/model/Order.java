package com.example.guarani.sistemas.demo.domain.model;

import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;
import com.example.guarani.sistemas.demo.domain.enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime creationDate;
    private BigDecimal totalAmount;
    private OrderStatus status; // Enum: PENDING, COMPLETED, CANCELLED

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items; // Lista de produtos no pedido

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // Enum: PENDING, PAID, FAILED

    private LocalDateTime paymentDate;
    private BigDecimal discount;
    private BigDecimal shippingFee;

    public void updateTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;

        if (this.items != null){
            for (OrderItem item : items) {
                total = total.add(item.getTotalPrice());
            }

            // Aplica taxa de frete
            this.totalAmount = total.add(this.shippingFee);
        }
    }

    public void addDiscount(){
        if (this.totalAmount != null){
            this.totalAmount = this.totalAmount.multiply(this.discount);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }
}