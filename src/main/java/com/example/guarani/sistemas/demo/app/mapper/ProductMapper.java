package com.example.guarani.sistemas.demo.app.mapper;

import com.example.guarani.sistemas.demo.domain.model.Product;
import com.example.guarani.sistemas.demo.app.dto.product.ProductRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.product.ProductResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponseDTO toProductResponse(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory()
        );
    }

    public Product toProduct(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.name());
        product.setDescription(productRequestDTO.description());
        product.setPrice(productRequestDTO.price());
        product.setCategory(productRequestDTO.category());
        product.setStockQuantity(productRequestDTO.stockQuantity());
        return product;
    }
}