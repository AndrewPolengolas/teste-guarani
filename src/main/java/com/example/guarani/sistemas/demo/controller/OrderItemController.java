package com.example.guarani.sistemas.demo.controller;

import com.example.guarani.sistemas.demo.app.dto.orderItem.OrderItemRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.orderItem.OrderItemResponseDTO;
import com.example.guarani.sistemas.demo.app.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public ResponseEntity<?> createOrderItem(
            @PathVariable Long orderId,
            @RequestBody OrderItemRequestDTO orderItemRequestDTO) {

        OrderItemResponseDTO dto = orderItemService.createOrderItem(orderItemRequestDTO, orderId);

        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping
    public ResponseEntity<?> getOrderItems(@PathVariable Long orderId) {
        List<OrderItemResponseDTO> dtos = orderItemService.getOrderItems(orderId);

        return ResponseEntity.ok().body(dtos);
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<?> deleteOrderItem(
            @PathVariable Long orderId, @PathVariable Long orderItemId) {

        orderItemService.deleteOrderItem(orderItemId);
        return ResponseEntity.noContent().build();
    }
}