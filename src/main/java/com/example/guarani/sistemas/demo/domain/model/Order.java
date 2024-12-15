package com.example.guarani.sistemas.demo.domain.model;

import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;
import com.example.guarani.sistemas.demo.domain.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "customer_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date creationDate;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Date paymentDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount must be at least 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "Discount must be at most 1.0")
    private BigDecimal discount;
    private BigDecimal shippingFee;

    public void updateTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;

        if (this.items != null){
            for (OrderItem item : items) {
                total = total.add(item.getTotalPrice());
            }

            this.totalAmount = total.add(this.shippingFee);
        }
    }

    public void addDiscount(){
        System.out.println("Adicionando disconto");
        if (this.totalAmount != null){
            BigDecimal discount = this.totalAmount.multiply(this.discount);

            this.totalAmount = this.totalAmount.subtract(discount);
        }
    }
}