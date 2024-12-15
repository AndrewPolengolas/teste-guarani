//package com.example.guarani.sistemas.demo.app.service;
//
//import com.example.guarani.sistemas.demo.app.dto.product.ProductRequestDTO;
//import com.example.guarani.sistemas.demo.app.dto.product.ProductResponseDTO;
//import com.example.guarani.sistemas.demo.app.mapper.ProductMapper;
//import com.example.guarani.sistemas.demo.domain.model.Product;
//import com.example.guarani.sistemas.demo.domain.repository.ProductRepository;
//import com.example.guarani.sistemas.demo.infra.exceptions.ResourceNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ProductServiceTest {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private ProductMapper productMapper;
//
//    @InjectMocks
//    private ProductService productService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void shouldCreateProduct() {
//        // Arrange
//        ProductRequestDTO requestDTO = new ProductRequestDTO("Product 1", "Description", 100.0, "Category");
//        Product product = new Product(1L, "Product 1", "Description", 100.0, "Category");
//        ProductResponseDTO responseDTO = new ProductResponseDTO(1L, "Product 1", "Description", 100.0, "Category");
//
//        when(productMapper.toProduct(requestDTO)).thenReturn(product);
//        when(productRepository.save(product)).thenReturn(product);
//        when(productMapper.toProductResponse(product)).thenReturn(responseDTO);
//
//        // Act
//        ProductResponseDTO result = productService.createProduct(requestDTO);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("Product 1", result.name());
//        verify(productRepository, times(1)).save(product);
//        verify(productMapper, times(1)).toProductResponse(product);
//    }
//
//    @Test
//    void shouldGetProductById() {
//        // Arrange
//        Long productId = 1L;
//        Product product = new Product(productId, "Product 1", "Description", 100.0, "Category");
//        ProductResponseDTO responseDTO = new ProductResponseDTO(productId, "Product 1", "Description", 100.0, "Category");
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(productMapper.toProductResponse(product)).thenReturn(responseDTO);
//
//        // Act
//        ProductResponseDTO result = productService.getProductById(productId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("Product 1", result.name());
//        verify(productRepository, times(1)).findById(productId);
//    }
//
//    @Test
//    void shouldThrowExceptionWhenProductNotFoundById() {
//        // Arrange
//        Long productId = 1L;
//        when(productRepository.findById(productId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(productId));
//        verify(productRepository, times(1)).findById(productId);
//    }
//
//    @Test
//    void shouldUpdateProduct() {
//        // Arrange
//        Long productId = 1L;
//        ProductRequestDTO requestDTO = new ProductRequestDTO("Updated Product", "Updated Description", 200.0, "Updated Category");
//        Product existingProduct = new Product(productId, "Old Product", "Old Description", 100.0, "Old Category");
//        Product updatedProduct = new Product(productId, "Updated Product", "Updated Description", 200.0, "Updated Category");
//        ProductResponseDTO responseDTO = new ProductResponseDTO(productId, "Updated Product", "Updated Description", 200.0, "Updated Category");
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
//        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
//        when(productMapper.toProductResponse(updatedProduct)).thenReturn(responseDTO);
//
//        // Act
//        ProductResponseDTO result = productService.updateProduct(productId, requestDTO);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("Updated Product", result.name());
//        verify(productRepository, times(1)).findById(productId);
//        verify(productRepository, times(1)).save(existingProduct);
//    }
//
//    @Test
//    void shouldDeleteProduct() {
//        // Arrange
//        Long productId = 1L;
//        Product product = new Product(productId, "Product 1", "Description", 100.0, "Category");
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        doNothing().when(productRepository).delete(product);
//
//        // Act
//        productService.deleteProduct(productId);
//
//        // Assert
//        verify(productRepository, times(1)).findById(productId);
//        verify(productRepository, times(1)).delete(product);
//    }
//
//    @Test
//    void shouldGetAllProducts() {
//        // Arrange
//        Product product1 = new Product(1L, "Product 1", "Description 1", 100.0, "Category 1");
//        Product product2 = new Product(2L, "Product 2", "Description 2", 200.0, "Category 2");
//        ProductResponseDTO response1 = new ProductResponseDTO(1L, "Product 1", "Description 1", 100.0, "Category 1");
//        ProductResponseDTO response2 = new ProductResponseDTO(2L, "Product 2", "Description 2", 200.0, "Category 2");
//
//        when(productRepository.findAll()).thenReturn(List.of(product1, product2));
//        when(productMapper.toProductResponse(product1)).thenReturn(response1);
//        when(productMapper.toProductResponse(product2)).thenReturn(response2);
//
//        // Act
//        List<ProductResponseDTO> result = productService.getAllProducts();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        verify(productRepository, times(1)).findAll();
//        verify(productMapper, times(1)).toProductResponse(product1);
//        verify(productMapper, times(1)).toProductResponse(product2);
//    }
//}
