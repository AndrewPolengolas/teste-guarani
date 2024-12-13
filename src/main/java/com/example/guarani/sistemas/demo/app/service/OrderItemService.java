package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.mapper.OrderItemMapper;
import com.example.guarani.sistemas.demo.app.dto.orderItem.OrderItemRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.orderItem.OrderItemResponseDTO;
import com.example.guarani.sistemas.demo.domain.model.Order;
import com.example.guarani.sistemas.demo.domain.model.OrderItem;
import com.example.guarani.sistemas.demo.domain.model.Product;
import com.example.guarani.sistemas.demo.domain.repository.OrderItemRepository;
import com.example.guarani.sistemas.demo.domain.repository.OrderRepository;
import com.example.guarani.sistemas.demo.domain.repository.ProductRepository;
import com.example.guarani.sistemas.demo.infra.exceptions.ResourceNotFoundException;
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

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(orderItemRequestDTO.quantity());

        orderItem.setOrder(order);

        orderItem.calculateTotalPrice();

        orderItem = orderItemRepository.save(orderItem);

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
    }
}