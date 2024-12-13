package com.example.guarani.sistemas.demo.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private BigDecimal totalPrice; // Preço total do item (itemPrice * quantity)

    public OrderItem(Order order, Product product, int quantity, BigDecimal itemPrice) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }

    public OrderItem() {}

    public BigDecimal calculateTotalPrice() {
        this.totalPrice = this.product.getPrice().multiply(BigDecimal.valueOf(quantity));
        return this.totalPrice;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice != null ? totalPrice : calculateTotalPrice();
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
