package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.order.OrderFilterDTO;
import com.example.guarani.sistemas.demo.app.mapper.OrderMapper;
import com.example.guarani.sistemas.demo.app.dto.order.OrderRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.order.OrderResponseDTO;
import com.example.guarani.sistemas.demo.domain.model.Order;
import com.example.guarani.sistemas.demo.domain.repository.OrderRepository;
import com.example.guarani.sistemas.demo.infra.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        Order order = orderMapper.toOrder(orderRequestDTO);
        order.updateTotalAmount();
        order.addDiscount();
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
}