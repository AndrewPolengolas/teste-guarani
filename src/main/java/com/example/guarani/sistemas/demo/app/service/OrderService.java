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
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

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

        Customer customer = customerRepository.findById(orderRequestDTO.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + orderRequestDTO.customerId()));

        Order order = orderMapper.toOrder(orderRequestDTO, customer);
        order.setStatus(OrderStatus.OPEN);
        order.setCreationDate(new Date());
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponseDTO(savedOrder);
    }

    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return orderMapper.toOrderResponseDTO(order);
    }

    public List<OrderResponseDTO> getAllOrders(OrderFilterDTO filter) {
        List<Order> orders;

        if (filter == null) {
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

        return orders.stream()
                .map(orderMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO closeOrderById(Long id, OrderPaymentDTO orderPaymentDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        checkOrderStatus(order.getStatus());
        checkOrderItens(order);

        order.setStatus(OrderStatus.WAITING_PAYMENT);
        PaymentStrategy strategy = paymentStrategies.get(orderPaymentDTO.paymentType());

        strategy.sendPayment(order);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponseDTO(savedOrder);
    }

    @Transactional
    public void updatePayment(ProcessedOrderMessage message) {
        Order order = orderRepository.findById(message.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + message.getOrderId()));

        if (message.isSuccess()) {
            updatePaymentStatus(order, message.getPaymentDate(), OrderStatus.COMPLETED, PaymentStatus.PAID);

            List<OrderItem> items = order.getItems();

            items.stream().forEach(
                    item -> {
                        Product product = item.getProduct();
                        product.updateStock(item.getQuantity());
                        productRepository.save(product);
                    }
            );
        } else {
            updatePaymentStatus(order, message.getPaymentDate(), OrderStatus.CANCELLED, PaymentStatus.FAILED);
        }

        orderRepository.save(order);
    }

    public void checkOrderItens(Order order) {
        if (order.getItems().isEmpty())
            throw new IllegalArgumentException("There must be items in the order for it to be closed.");
    }

    public void checkOrderStatus(OrderStatus orderStatus) {
        if (!orderStatus.equals(OrderStatus.OPEN))
            throw new IllegalArgumentException("The order must be open to be closed.");
    }

    public Order updatePaymentStatus(Order order, Date paymantDate, OrderStatus orderStatus, PaymentStatus paymentStatus){
        order.setStatus(orderStatus);
        order.setPaymentDate(paymantDate);
        order.setPaymentStatus(paymentStatus);

        return order;
    }
}