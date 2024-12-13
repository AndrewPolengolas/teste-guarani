package com.example.guarani.sistemas.demo.controller;

import com.example.guarani.sistemas.demo.app.dto.order.OrderFilterDTO;
import com.example.guarani.sistemas.demo.app.dto.order.OrderRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.order.OrderResponseDTO;
import com.example.guarani.sistemas.demo.app.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO dto = orderService.createOrder(orderRequestDTO);

        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        OrderResponseDTO dto = orderService.getOrderById(id);

        return ResponseEntity.ok().body(dto);
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(OrderFilterDTO filter) {
        List<OrderResponseDTO> dtos = orderService.getAllOrders(filter);
        return ResponseEntity.ok().body(dtos);
    }
}