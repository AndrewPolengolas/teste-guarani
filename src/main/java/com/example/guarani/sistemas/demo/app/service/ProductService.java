package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.product.ProductFilterDTO;
import com.example.guarani.sistemas.demo.app.mapper.ProductMapper;
import com.example.guarani.sistemas.demo.domain.model.Product;
import com.example.guarani.sistemas.demo.app.dto.product.ProductRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.product.ProductResponseDTO;
import com.example.guarani.sistemas.demo.domain.repository.ProductRepository;
import com.example.guarani.sistemas.demo.infra.exceptions.custom.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = productMapper.toProduct(productRequestDTO);
        product = productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    public ProductResponseDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toProductResponse(product);
    }

    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setName(productRequestDTO.name());
        product.setDescription(productRequestDTO.description());
        product.setPrice(productRequestDTO.price());
        product.setCategory(productRequestDTO.category());

        product = productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    public List<ProductResponseDTO> getAllProducts(ProductFilterDTO filter) {

        List<Product> products;

        if (filter == null) {
            products = productRepository.findAll();
        } else {

            products = productRepository.findByFilters(
                    filter.minPrice() != null ? filter.minPrice() : null,
                    filter.maxPrice() != null ? filter.maxPrice() : null,
                    filter.category() != null ? filter.category().toString() : null
            );
        }

        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
