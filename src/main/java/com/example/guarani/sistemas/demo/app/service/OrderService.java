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
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final Map<String, PaymentStrategy> paymentStrategies;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, Map<String, PaymentStrategy> paymentStrategies, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.paymentStrategies = paymentStrategies;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        logger.info("Creating order for customer ID: {}", orderRequestDTO.customerId());

        Customer customer = customerRepository.findById(orderRequestDTO.customerId())
                .orElseThrow(() -> {
                    logger.warn("Customer not found with ID: {}", orderRequestDTO.customerId());
                    return new ResourceNotFoundException("Customer not found with id: " + orderRequestDTO.customerId());
                });

        Order order = orderMapper.toOrder(orderRequestDTO, customer);
        order.setStatus(OrderStatus.OPEN);
        order.setCreationDate(new Date());
        Order savedOrder = orderRepository.save(order);

        logger.info("Order created successfully with ID: {}", savedOrder.getId());
        return orderMapper.toOrderResponseDTO(savedOrder);
    }

    public OrderResponseDTO getOrderById(Long id) {
        logger.info("Fetching order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Order not found with ID: {}", id);
                    return new ResourceNotFoundException("Order not found with id: " + id);
                });

        logger.info("Order fetched successfully with ID: {}", id);
        return orderMapper.toOrderResponseDTO(order);
    }

    public List<OrderResponseDTO> getAllOrders(OrderFilterDTO filter) {
        logger.info("Fetching all orders with filter: {}", filter);

        List<Order> orders;

        if (filter.status() == null &&
                filter.startDate() == null &&
                filter.endDate() == null &&
                filter.maxAmount() == null &&
                filter.minAmount() == null) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findByFilters(
                    filter.status() != null ? filter.status().toString() : null,
                    filter.startDate() != null ? filter.startDate() : null,
                    filter.endDate() != null ? filter.endDate() : null,
                    filter.minAmount() != null ? filter.minAmount() : null,
                    filter.maxAmount() != null ? filter.maxAmount() : null
            );
        }

        logger.info("Fetched {} orders", orders.size());
        return orders.stream()
                .map(orderMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO closeOrderById(Long id, OrderPaymentDTO orderPaymentDTO) {
        logger.info("Closing order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Order not found with ID: {}", id);
                    return new ResourceNotFoundException("Order not found with id: " + id);
                });

        checkOrderStatus(order.getStatus());
        checkOrderItens(order);

        order.setStatus(OrderStatus.WAITING_PAYMENT);
        logger.info("Order status updated to WAITING_PAYMENT for ID: {}", id);

        PaymentStrategy strategy = paymentStrategies.get(orderPaymentDTO.paymentType());
        strategy.sendPayment(order);

        Order savedOrder = orderRepository.save(order);
        logger.info("Order closed successfully with ID: {}", savedOrder.getId());
        return orderMapper.toOrderResponseDTO(savedOrder);
    }

    @Transactional
    public void updatePayment(ProcessedOrderMessage message) {
        logger.info("Updating payment for order ID: {}", message.getOrderId());

        Order order = orderRepository.findById(message.getOrderId())
                .orElseThrow(() -> {
                    logger.warn("Order not found with ID: {}", message.getOrderId());
                    return new ResourceNotFoundException("Order not found with id: " + message.getOrderId());
                });

        if (message.isSuccess()) {
            logger.info("Payment succeeded for order ID: {}", message.getOrderId());
            updatePaymentStatus(order, message.getPaymentDate(), OrderStatus.COMPLETED, PaymentStatus.PAID);

            order.getItems().forEach(item -> {
                Product product = item.getProduct();
                product.updateStock(item.getQuantity());
                productRepository.save(product);
                logger.info("Stock updated for product ID: {}", product.getId());
            });
        } else {
            logger.warn("Payment failed for order ID: {}", message.getOrderId());
            updatePaymentStatus(order, message.getPaymentDate(), OrderStatus.CANCELLED, PaymentStatus.FAILED);
        }

        orderRepository.save(order);
        logger.info("Payment update completed for order ID: {}", message.getOrderId());
    }

    public void checkOrderItens(Order order) {
        if (order.getItems().isEmpty()) {
            logger.warn("Order ID: {} has no items to be closed", order.getId());
            throw new IllegalArgumentException("There must be items in the order for it to be closed.");
        }
    }

    public void checkOrderStatus(OrderStatus orderStatus) {
        if (!orderStatus.equals(OrderStatus.OPEN)) {
            logger.warn("Order cannot be closed because status is not OPEN. Current status: {}", orderStatus);
            throw new IllegalArgumentException("The order must be open to be closed.");
        }
    }

    public Order updatePaymentStatus(Order order, Date paymentDate, OrderStatus orderStatus, PaymentStatus paymentStatus) {
        logger.info("Updating payment status for order ID: {}", order.getId());
        order.setStatus(orderStatus);
        order.setPaymentDate(paymentDate);
        order.setPaymentStatus(paymentStatus);
        return order;
    }
}
