package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.order.OrderFilterDTO;
import com.example.guarani.sistemas.demo.app.dto.order.OrderPaymentDTO;
import com.example.guarani.sistemas.demo.app.mapper.OrderMapper;
import com.example.guarani.sistemas.demo.app.dto.order.OrderRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.order.OrderResponseDTO;
import com.example.guarani.sistemas.demo.app.service.strategy.PaymentStrategy;
import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;
import com.example.guarani.sistemas.demo.domain.model.Order;
import com.example.guarani.sistemas.demo.domain.repository.OrderRepository;
import com.example.guarani.sistemas.demo.infra.config.OrderRabbitConfig;
import com.example.guarani.sistemas.demo.infra.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AmqpTemplate amqpTemplate;
    private final Map<String, PaymentStrategy> paymentStrategies;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, AmqpTemplate amqpTemplate, Map<String, PaymentStrategy> paymentStrategies) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.amqpTemplate = amqpTemplate;
        this.paymentStrategies = paymentStrategies;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        Order order = orderMapper.toOrder(orderRequestDTO);
        order.updateTotalAmount();
        order.addDiscount();
        order.setStatus(OrderStatus.OPEN);
        order.setCreationDate(new Date());
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponseDTO(savedOrder);
    }

    public OrderResponseDTO getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.map(orderMapper::toOrderResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public List<OrderResponseDTO> getAllOrders(OrderFilterDTO filter) {
        List<Order> orders;

        if (filter == null) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findByFilters(
                    filter.status(),
                    filter.startDate(),
                    filter.endDate(),
                    filter.minAmount(),
                    filter.maxAmount()
            );
        }

        return orders.stream()
                .map(orderMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO closeOrderById(Long id, OrderPaymentDTO orderPaymentDTO) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order != null && order.getTotalAmount() != null){
            order.setStatus(OrderStatus.WAITING_PAYMENT);
            PaymentStrategy strategy = paymentStrategies.get(orderPaymentDTO.paymentType());

            strategy.sendPayment(order);
        }

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponseDTO(savedOrder);
    }

}