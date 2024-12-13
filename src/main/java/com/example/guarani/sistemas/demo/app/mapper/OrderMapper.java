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

    public Order toOrder(OrderRequestDTO orderRequestDTO) {
        Order order = new Order();
        order.setCustomer(new Customer());
        order.getCustomer().setId(orderRequestDTO.customerId());
        order.setDiscount(orderRequestDTO.discount());
        order.setShippingFee(orderRequestDTO.shippingFee());
        order.setStatus(orderRequestDTO.status());

        order.setItems(orderRequestDTO.productIds().stream()
                .map(productId -> new OrderItem(null, null, 1, BigDecimal.ZERO))
                .collect(Collectors.toList())
        );
        return order;
    }

    public OrderResponseDTO toOrderResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getCustomer().getId(),
                order.getItems().stream().map(orderItemMapper::toOrderItemResponseDTO).collect(Collectors.toList()),
                order.getTotalAmount(),
                order.getDiscount(),
                order.getShippingFee(),
                order.getStatus(),
                order.getCreationDate()
        );
    }
}