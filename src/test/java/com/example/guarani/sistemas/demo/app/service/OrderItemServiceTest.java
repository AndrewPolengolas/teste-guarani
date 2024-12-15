package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.orderItem.OrderItemRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.orderItem.OrderItemResponseDTO;
import com.example.guarani.sistemas.demo.app.mapper.OrderItemMapper;
import com.example.guarani.sistemas.demo.domain.model.Order;
import com.example.guarani.sistemas.demo.domain.model.OrderItem;
import com.example.guarani.sistemas.demo.domain.model.Product;
import com.example.guarani.sistemas.demo.domain.repository.OrderItemRepository;
import com.example.guarani.sistemas.demo.domain.repository.OrderRepository;
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

class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderItemService orderItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrderItemSuccess() {
        OrderItemRequestDTO requestDTO = new OrderItemRequestDTO(1L, 2);
        Order order = new Order();
        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.TEN);
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setOrder(order);

        OrderItem savedOrderItem = new OrderItem();
        savedOrderItem.setId(1L);

        OrderItemResponseDTO responseDTO = new OrderItemResponseDTO(1L, 1L, "Product Name", 2, BigDecimal.TEN, BigDecimal.valueOf(20));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedOrderItem);
        when(orderItemMapper.toOrderItemResponseDTO(savedOrderItem)).thenReturn(responseDTO);

        OrderItemResponseDTO result = orderItemService.createOrderItem(requestDTO, 1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void testCreateOrderItemOrderNotFound() {
        OrderItemRequestDTO requestDTO = new OrderItemRequestDTO(1L, 2);

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemService.createOrderItem(requestDTO, 1L));
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateOrderItemProductNotFound() {
        OrderItemRequestDTO requestDTO = new OrderItemRequestDTO(1L, 2);
        Order order = new Order();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemService.createOrderItem(requestDTO, 1L));
        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderItemsSuccess() {
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(2L);

        OrderItemResponseDTO responseDTO1 = new OrderItemResponseDTO(1L, 1L, "Product Name 1", 1, BigDecimal.TEN, BigDecimal.TEN);
        OrderItemResponseDTO responseDTO2 = new OrderItemResponseDTO(2L, 2L, "Product Name 2", 2, BigDecimal.TEN, BigDecimal.valueOf(20));

        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of(orderItem1, orderItem2));
        when(orderItemMapper.toOrderItemResponseDTO(orderItem1)).thenReturn(responseDTO1);
        when(orderItemMapper.toOrderItemResponseDTO(orderItem2)).thenReturn(responseDTO2);

        List<OrderItemResponseDTO> result = orderItemService.getOrderItems(1L);

        assertEquals(2, result.size());
        verify(orderItemRepository, times(1)).findByOrderId(1L);
    }

    @Test
    void testDeleteOrderItemSuccess() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        Order order = new Order();
        orderItem.setOrder(order);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));

        orderItemService.deleteOrderItem(1L);

        verify(orderItemRepository, times(1)).delete(orderItem);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testDeleteOrderItemNotFound() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemService.deleteOrderItem(1L));
        verify(orderItemRepository, times(1)).findById(1L);
    }
}
