package com.example.guarani.sistemas.demo.app.mapper;

import com.example.guarani.sistemas.demo.app.dto.order.OrderRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.order.OrderResponseDTO;
import com.example.guarani.sistemas.demo.domain.model.Customer;
import com.example.guarani.sistemas.demo.domain.model.Order;
import com.example.guarani.sistemas.demo.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderMapper(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    public Order toOrder(OrderRequestDTO orderRequestDTO, Customer customer) {
        return Order.builder()
                .customer(customer)
                .discount(orderRequestDTO.discount())
                .shippingFee(orderRequestDTO.shippingFee())
                .build();
    }

    public OrderResponseDTO toOrderResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getCustomer().getId(),
                order.getItems() != null ? order.getItems().stream().map(orderItemMapper::toOrderItemResponseDTO).collect(Collectors.toList()) : null,
                order.getTotalAmount(),
                order.getDiscount(),
                order.getShippingFee(),
                order.getStatus(),
                order.getCreationDate()
        );
    }
}