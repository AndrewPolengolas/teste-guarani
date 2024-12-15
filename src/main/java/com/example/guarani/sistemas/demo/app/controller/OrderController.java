package com.example.guarani.sistemas.demo.app.controller;

import com.example.guarani.sistemas.demo.app.dto.order.OrderFilterDTO;
import com.example.guarani.sistemas.demo.app.dto.order.OrderPaymentDTO;
import com.example.guarani.sistemas.demo.app.dto.order.OrderRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.order.OrderResponseDTO;
import com.example.guarani.sistemas.demo.app.service.OrderService;
import com.example.guarani.sistemas.demo.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Endpoints for managing orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_OPERATOR')")
    @Operation(
            summary = "Create a new order",
            description = "Creates a new order using the provided information. Requires ADMIN or OPERATOR permissions.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Order successfully created",
                            content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO dto = orderService.createOrder(orderRequestDTO);
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_OPERATOR')")
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves details of an order by its unique ID. Requires ADMIN or OPERATOR permissions.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order found",
                            content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        OrderResponseDTO dto = orderService.getOrderById(id);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_OPERATOR')")
    @Operation(
            summary = "Get all orders",
            description = "Retrieves a list of all orders based on the provided filter. Requires ADMIN or OPERATOR permissions.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of orders",
                            content = @Content(schema = @Schema(implementation = OrderResponseDTO.class)))
            }
    )
    public ResponseEntity<?> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount) {

        OrderFilterDTO filter = new OrderFilterDTO(status, startDate, endDate, minAmount, maxAmount);

        List<OrderResponseDTO> dtos = orderService.getAllOrders(filter);
        return ResponseEntity.ok().body(dtos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_OPERATOR')")
    @Operation(
            summary = "Close an order",
            description = "Closes an order by its unique ID using the provided payment details. Requires ADMIN or OPERATOR permissions.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order successfully closed",
                            content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid payment details")
            }
    )
    public ResponseEntity<?> closeOrderById(@PathVariable Long id,
                                            @RequestBody OrderPaymentDTO orderPaymentDTO) {
        OrderResponseDTO dto = orderService.closeOrderById(id, orderPaymentDTO);
        return ResponseEntity.ok().body(dto);
    }
}
