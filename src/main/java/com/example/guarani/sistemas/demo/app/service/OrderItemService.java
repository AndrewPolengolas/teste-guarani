package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.mapper.OrderItemMapper;
import com.example.guarani.sistemas.demo.app.dto.orderItem.OrderItemRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.orderItem.OrderItemResponseDTO;
import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;
import com.example.guarani.sistemas.demo.domain.model.Order;
import com.example.guarani.sistemas.demo.domain.model.OrderItem;
import com.example.guarani.sistemas.demo.domain.model.Product;
import com.example.guarani.sistemas.demo.domain.repository.OrderItemRepository;
import com.example.guarani.sistemas.demo.domain.repository.OrderRepository;
import com.example.guarani.sistemas.demo.domain.repository.ProductRepository;
import com.example.guarani.sistemas.demo.infra.exceptions.custom.OutOfStockException;
import com.example.guarani.sistemas.demo.infra.exceptions.custom.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository, ProductRepository productRepository,
                            OrderRepository orderRepository, OrderItemMapper orderItemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemMapper = orderItemMapper;
    }

    @Transactional
    public OrderItemResponseDTO createOrderItem(OrderItemRequestDTO orderItemRequestDTO, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Product product = productRepository.findById(orderItemRequestDTO.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        checkOrderStatus(order.getStatus());
        checkStock(product, orderItemRequestDTO.quantity());

        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(orderItemRequestDTO.quantity())
                .order(order)
                .build();

        orderItem.calculateTotalPrice();

        orderItem = orderItemRepository.save(orderItem);

        updateOrder(order);

        return orderItemMapper.toOrderItemResponseDTO(orderItem);
    }

    public List<OrderItemResponseDTO> getOrderItems(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        return orderItems.stream()
                .map(orderItemMapper::toOrderItemResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrderItem(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found"));

        orderItemRepository.delete(orderItem);

        Order order = orderItem.getOrder();

        updateOrder(order);
    }

    public void checkStock(Product product, int quantity) {
        if (product.getStockQuantity() < quantity)
            throw new OutOfStockException("Product '" + product.getName() + "' is out of stock.");
    }

    public void checkOrderStatus(OrderStatus orderStatus) {
        if (!orderStatus.equals(OrderStatus.OPEN))
            throw new IllegalArgumentException("OrderItem cannot be added to orders with status different from OPEN.");
    }

    @Transactional
    public void updateOrder(Order order) {
        order.updateTotalAmount();
        order.addDiscount();

        orderRepository.save(order);
    }
}