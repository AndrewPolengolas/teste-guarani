package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.product.ProductFilterDTO;
import com.example.guarani.sistemas.demo.app.mapper.ProductMapper;
import com.example.guarani.sistemas.demo.domain.model.Product;
import com.example.guarani.sistemas.demo.app.dto.product.ProductRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.product.ProductResponseDTO;
import com.example.guarani.sistemas.demo.domain.repository.ProductRepository;
import com.example.guarani.sistemas.demo.infra.exceptions.custom.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        logger.info("Creating product with name: {}", productRequestDTO.name());
        Product product = productMapper.toProduct(productRequestDTO);
        product = productRepository.save(product);
        logger.info("Product created successfully with ID: {}", product.getId());
        return productMapper.toProductResponse(product);
    }

    public ProductResponseDTO getProductById(Long productId) {
        logger.info("Fetching product with ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", productId);
                    return new ResourceNotFoundException("Product not found");
                });
        logger.info("Product fetched successfully with ID: {}", productId);
        return productMapper.toProductResponse(product);
    }

    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
        logger.info("Updating product with ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", productId);
                    return new ResourceNotFoundException("Product not found");
                });

        logger.info("Updating product details for ID: {}", productId);
        product.setName(productRequestDTO.name());
        product.setDescription(productRequestDTO.description());
        product.setPrice(productRequestDTO.price());
        product.setCategory(productRequestDTO.category());

        product = productRepository.save(product);
        logger.info("Product updated successfully with ID: {}", productId);
        return productMapper.toProductResponse(product);
    }

    public void deleteProduct(Long productId) {
        logger.info("Deleting product with ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", productId);
                    return new ResourceNotFoundException("Product not found");
                });
        productRepository.delete(product);
        logger.info("Product deleted successfully with ID: {}", productId);
    }

    public List<ProductResponseDTO> getAllProducts(ProductFilterDTO filter) {
        logger.info("Fetching all products with filter: {}", filter);

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

        logger.info("Fetched {} products", products.size());
        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
