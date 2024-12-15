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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemService.class);

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
        logger.info("Creating order item for order ID: {} and product ID: {}", orderId, orderItemRequestDTO.productId());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.warn("Order not found with ID: {}", orderId);
                    return new ResourceNotFoundException("Order not found");
                });

        Product product = productRepository.findById(orderItemRequestDTO.productId())
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", orderItemRequestDTO.productId());
                    return new ResourceNotFoundException("Product not found");
                });

        checkOrderStatus(order.getStatus());
        checkStock(product, orderItemRequestDTO.quantity());

        logger.info("Adding product '{}' (quantity: {}) to order ID: {}", product.getName(), orderItemRequestDTO.quantity(), orderId);
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(orderItemRequestDTO.quantity())
                .order(order)
                .build();

        orderItem.calculateTotalPrice();
        orderItem = orderItemRepository.save(orderItem);

        logger.info("Order item created successfully with ID: {}", orderItem.getId());

        updateOrder(order);
        return orderItemMapper.toOrderItemResponseDTO(orderItem);
    }

    public List<OrderItemResponseDTO> getOrderItems(Long orderId) {
        logger.info("Fetching order items for order ID: {}", orderId);

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        logger.info("Fetched {} order items for order ID: {}", orderItems.size(), orderId);
        return orderItems.stream()
                .map(orderItemMapper::toOrderItemResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrderItem(Long orderItemId) {
        logger.info("Deleting order item with ID: {}", orderItemId);

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> {
                    logger.warn("Order item not found with ID: {}", orderItemId);
                    return new ResourceNotFoundException("OrderItem not found");
                });

        orderItemRepository.delete(orderItem);
        logger.info("Order item deleted successfully with ID: {}", orderItemId);

        Order order = orderItem.getOrder();
        updateOrder(order);
    }

    public void checkStock(Product product, int quantity) {
        if (product.getStockQuantity() < quantity) {
            logger.warn("Product '{}' is out of stock. Available: {}, Requested: {}", product.getName(), product.getStockQuantity(), quantity);
            throw new OutOfStockException("Product '" + product.getName() + "' is out of stock.");
        }
    }

    public void checkOrderStatus(OrderStatus orderStatus) {
        if (!orderStatus.equals(OrderStatus.OPEN)) {
            logger.warn("Order status '{}' is invalid for adding order items. Must be 'OPEN'.", orderStatus);
            throw new IllegalArgumentException("OrderItem cannot be added to orders with status different from OPEN.");
        }
    }

    @Transactional
    public void updateOrder(Order order) {
        logger.info("Updating total amount and discount for order ID: {}", order.getId());

        order.updateTotalAmount();
        order.addDiscount();
        orderRepository.save(order);

        logger.info("Order updated successfully with ID: {}", order.getId());
    }
}
