package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.product.ProductFilterDTO;
import com.example.guarani.sistemas.demo.app.dto.product.ProductRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.product.ProductResponseDTO;
import com.example.guarani.sistemas.demo.app.mapper.ProductMapper;
import com.example.guarani.sistemas.demo.domain.enums.Category;
import com.example.guarani.sistemas.demo.domain.model.Product;
import com.example.guarani.sistemas.demo.domain.repository.ProductRepository;
import com.example.guarani.sistemas.demo.infra.exceptions.custom.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct() {
        ProductRequestDTO requestDTO = new ProductRequestDTO("Test Product", "Description", BigDecimal.TEN, 5, Category.ELECTRONICS);
        Product product = new Product(null, "Test Product", "Description", BigDecimal.TEN, Category.ELECTRONICS, 5);
        Product savedProduct = new Product(1L, "Test Product", "Description", BigDecimal.TEN, Category.ELECTRONICS, 5);
        ProductResponseDTO responseDTO = new ProductResponseDTO(1L, "Test Product", "Description", BigDecimal.TEN, 5, Category.ELECTRONICS);

        when(productMapper.toProduct(requestDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toProductResponse(savedProduct)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.createProduct(requestDTO);

        assertEquals(responseDTO, result);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testGetProductById() {
        Product product = new Product(1L, "Test Product", "Description", BigDecimal.TEN, Category.ELECTRONICS, 5);
        ProductResponseDTO responseDTO = new ProductResponseDTO(1L, "Test Product", "Description", BigDecimal.TEN, 5, Category.ELECTRONICS);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toProductResponse(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.getProductById(1L);

        assertEquals(responseDTO, result);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateProduct() {
        ProductRequestDTO requestDTO = new ProductRequestDTO("Updated Product", "Updated Description", BigDecimal.valueOf(20), 10, Category.ELECTRONICS);
        Product existingProduct = new Product(1L, "Test Product", "Description", BigDecimal.TEN, Category.ELECTRONICS, 5);
        Product updatedProduct = new Product(1L, "Updated Product", "Updated Description", BigDecimal.valueOf(20), Category.ELECTRONICS, 10);
        ProductResponseDTO responseDTO = new ProductResponseDTO(1L, "Updated Product", "Updated Description", BigDecimal.valueOf(20), 10, Category.ELECTRONICS);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(productMapper.toProductResponse(updatedProduct)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.updateProduct(1L, requestDTO);

        assertEquals(responseDTO, result);
        assertEquals("Updated Product", existingProduct.getName());
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testDeleteProduct() {
        Product product = new Product(1L, "Test Product", "Description", BigDecimal.TEN, Category.ELECTRONICS, 5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllProductsWithoutFilter() {
        Product product1 = new Product(1L, "Product 1", "Description 1", BigDecimal.TEN, Category.ELECTRONICS, 5);
        Product product2 = new Product(2L, "Product 2", "Description 2", BigDecimal.valueOf(20), Category.BOOKS, 10);
        ProductResponseDTO responseDTO1 = new ProductResponseDTO(1L, "Product 1", "Description 1", BigDecimal.TEN, 5, Category.ELECTRONICS);
        ProductResponseDTO responseDTO2 = new ProductResponseDTO(2L, "Product 2", "Description 2", BigDecimal.valueOf(20), 10, Category.BOOKS);

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));
        when(productMapper.toProductResponse(product1)).thenReturn(responseDTO1);
        when(productMapper.toProductResponse(product2)).thenReturn(responseDTO2);

        ProductFilterDTO filter = null;
        List<ProductResponseDTO> result = productService.getAllProducts(filter);

        assertEquals(2, result.size());
        assertTrue(result.contains(responseDTO1));
        assertTrue(result.contains(responseDTO2));
        verify(productRepository, times(1)).findAll();
        verify(productRepository, never()).findByFilters(any(), any(), any());
    }

    @Test
    void testGetAllProductsWithFilter() {
        ProductFilterDTO filter = new ProductFilterDTO(BigDecimal.TEN, BigDecimal.valueOf(20), Category.ELECTRONICS);

        Product product = new Product(1L, "Product 1", "Description Product", BigDecimal.TEN, Category.ELECTRONICS, 5);
        ProductResponseDTO responseDTO = new ProductResponseDTO(1L, "Product 1", "Description Product", BigDecimal.TEN, 5, Category.ELECTRONICS);

        when(productRepository.findByFilters(
                eq(filter.minPrice()),
                eq(filter.maxPrice()),
                eq(filter.category().name())
        )).thenReturn(List.of(product));
        when(productMapper.toProductResponse(product)).thenReturn(responseDTO);

        List<ProductResponseDTO> result = productService.getAllProducts(filter);

        assertEquals(1, result.size());
        assertEquals(responseDTO, result.get(0));
        verify(productRepository, times(1)).findByFilters(
                eq(filter.minPrice()),
                eq(filter.maxPrice()),
                eq(filter.category().name())
        );
        verify(productRepository, never()).findAll();
    }

}
