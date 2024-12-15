package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.order.*;
import com.example.guarani.sistemas.demo.app.mapper.OrderMapper;
import com.example.guarani.sistemas.demo.app.service.strategy.PaymentStrategy;
import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;
import com.example.guarani.sistemas.demo.domain.enums.PaymentStatus;
import com.example.guarani.sistemas.demo.domain.model.Customer;
import com.example.guarani.sistemas.demo.domain.model.Order;
import com.example.guarani.sistemas.demo.domain.model.OrderItem;
import com.example.guarani.sistemas.demo.domain.model.Product;
import com.example.guarani.sistemas.demo.domain.repository.CustomerRepository;
import com.example.guarani.sistemas.demo.domain.repository.OrderRepository;
import com.example.guarani.sistemas.demo.domain.repository.ProductRepository;
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

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

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

        Customer customer = Customer.builder()
                .name("Test")
                .build();

        Order savedOrder = new Order();
        savedOrder.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(100);
        product.setPrice(BigDecimal.TEN);

        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(2)
                .build();

        orderItem.calculateTotalPrice();

        order.setItems(List.of(orderItem));

        OrderResponseDTO responseDTO = new OrderResponseDTO(1L, 1L, Collections.emptyList(), BigDecimal.valueOf(100), BigDecimal.valueOf(0.1), BigDecimal.valueOf(10.0), OrderStatus.OPEN, new Date());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderMapper.toOrder(orderRequestDTO, customer)).thenReturn(order);
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
        // Configuração do pedido
        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(BigDecimal.valueOf(100));
        order.setStatus(OrderStatus.OPEN);

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(100);
        product.setPrice(BigDecimal.TEN);

        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(2)
                .build();

        orderItem.calculateTotalPrice();

        order.setItems(List.of(orderItem));

        OrderPaymentDTO paymentDTO = new OrderPaymentDTO("CREDIT_CARD");
        PaymentStrategy strategy = mock(PaymentStrategy.class);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setStatus(OrderStatus.WAITING_PAYMENT);

        OrderResponseDTO responseDTO = new OrderResponseDTO(
                1L,
                null,
                null,
                BigDecimal.valueOf(100),
                null,
                null,
                OrderStatus.WAITING_PAYMENT,
                new Date()
        );

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(paymentStrategies.get(paymentDTO.paymentType())).thenReturn(strategy);
        when(orderMapper.toOrderResponseDTO(savedOrder)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.closeOrderById(1L, paymentDTO);

        verify(orderRepository, times(1)).save(order);
        verify(strategy, times(1)).sendPayment(order);
        assertNotNull(result, "Result should not be null");
        assertEquals(OrderStatus.WAITING_PAYMENT, result.status());
        assertEquals(1L, result.id());
    }



    @Test
    void testUpdatePaymentSuccess() {
        ProcessedOrderMessage message = new ProcessedOrderMessage();
        message.setOrderId(1L);
        message.setSuccess(true);
        message.setPaymentDate(new Date());

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(100);
        product.setPrice(BigDecimal.TEN);

        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(2)
                .build();

        orderItem.calculateTotalPrice();

        Order order = new Order();
        order.setId(1L);
        order.setItems(List.of(orderItem));
        order.setStatus(OrderStatus.WAITING_PAYMENT);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
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
