package com.example.guarani.sistemas.demo.domain.model;

import com.example.guarani.sistemas.demo.domain.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private int stockQuantity;

    public void updateStock(int quantity){
        if (quantity <= this.stockQuantity){
            this.stockQuantity = this.stockQuantity - quantity;
        }else {
            throw new IllegalArgumentException("There are not enough items in stock.");
        }
    }
}
