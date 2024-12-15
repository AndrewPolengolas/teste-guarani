package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.order.*;
import com.example.guarani.sistemas.demo.app.mapper.OrderMapper;
import com.example.guarani.sistemas.demo.app.service.strategy.PaymentStrategy;
import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;
import com.example.guarani.sistemas.demo.domain.enums.PaymentStatus;
import com.example.guarani.sistemas.demo.domain.model.Customer;
import com.example.guarani.sistemas.demo.domain.model.Order;
import com.example.guarani.sistemas.demo.domain.repository.OrderRepository;
import com.example.guarani.sistemas.demo.infra.exceptions.custom.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private Map<String, PaymentStrategy> paymentStrategies;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrderSuccess() {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(1L, List.of(1L, 2L), BigDecimal.valueOf(0.1), BigDecimal.valueOf(10.0));
        Order order = new Order();
        order.setStatus(OrderStatus.OPEN);

        Order savedOrder = new Order();
        savedOrder.setId(1L);

        OrderResponseDTO responseDTO = new OrderResponseDTO(1L, 1L, Collections.emptyList(), BigDecimal.valueOf(100), BigDecimal.valueOf(0.1), BigDecimal.valueOf(10.0), OrderStatus.OPEN, new Date());

        when(orderMapper.toOrder(orderRequestDTO, new Customer())).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(orderMapper.toOrderResponseDTO(savedOrder)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.createOrder(orderRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testGetOrderByIdSuccess() {
        Order order = new Order();
        order.setId(1L);
        OrderResponseDTO responseDTO = new OrderResponseDTO(1L, 1L, Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, OrderStatus.OPEN, new Date());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toOrderResponseDTO(order)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderByIdNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testCloseOrderByIdSuccess() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(BigDecimal.valueOf(100));
        order.setStatus(OrderStatus.OPEN);

        OrderPaymentDTO paymentDTO = new OrderPaymentDTO("CREDIT_CARD");
        PaymentStrategy strategy = mock(PaymentStrategy.class);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentStrategies.get(paymentDTO.paymentType())).thenReturn(strategy);

        orderService.closeOrderById(1L, paymentDTO);

        verify(orderRepository, times(1)).save(order);
        verify(strategy, times(1)).sendPayment(order);
        assertEquals(OrderStatus.WAITING_PAYMENT, order.getStatus());
    }

    @Test
    void testUpdatePaymentSuccess() {
        ProcessedOrderMessage message = new ProcessedOrderMessage();
        message.setOrderId(1L);
        message.setSuccess(true);
        message.setPaymentDate(new Date());

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.WAITING_PAYMENT);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.updatePayment(message);

        verify(orderRepository, times(1)).save(order);
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertEquals(PaymentStatus.PAID, order.getPaymentStatus());
        assertNotNull(order.getPaymentDate());
    }

    @Test
    void testUpdatePaymentFailure() {
        ProcessedOrderMessage message = new ProcessedOrderMessage();
        message.setOrderId(1L);
        message.setSuccess(false);
        message.setPaymentDate(new Date());

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.WAITING_PAYMENT);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.updatePayment(message);

        verify(orderRepository, times(1)).save(order);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(PaymentStatus.FAILED, order.getPaymentStatus());
        assertNotNull(order.getPaymentDate());
    }
}
