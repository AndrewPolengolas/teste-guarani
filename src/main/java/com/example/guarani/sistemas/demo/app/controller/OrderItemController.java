package com.example.guarani.sistemas.demo.app.controller;

import com.example.guarani.sistemas.demo.app.dto.orderItem.OrderItemRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.orderItem.OrderItemResponseDTO;
import com.example.guarani.sistemas.demo.app.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
@Tag(name = "Order Items Management", description = "Endpoints for managing items within orders")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_OPERATOR')")
    @Operation(
            summary = "Create a new order item",
            description = "Adds a new item to an existing order. Requires ADMIN or OPERATOR permissions.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Order item successfully created",
                            content = @Content(schema = @Schema(implementation = OrderItemResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    public ResponseEntity<?> createOrderItem(
            @PathVariable Long orderId,
            @RequestBody OrderItemRequestDTO orderItemRequestDTO) {
        OrderItemResponseDTO dto = orderItemService.createOrderItem(orderItemRequestDTO, orderId);
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_OPERATOR')")
    @Operation(
            summary = "Get items for an order",
            description = "Retrieves a list of all items for a specific order. Requires ADMIN or OPERATOR permissions.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the order items",
                            content = @Content(schema = @Schema(implementation = OrderItemResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    public ResponseEntity<?> getOrderItems(@PathVariable Long orderId) {
        List<OrderItemResponseDTO> dtos = orderItemService.getOrderItems(orderId);
        return ResponseEntity.ok().body(dtos);
    }

    @DeleteMapping("/{orderItemId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_OPERATOR')")
    @Operation(
            summary = "Delete an order item",
            description = "Removes an item from an order by its ID. Requires ADMIN or OPERATOR permissions.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Order item successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Order or order item not found")
            }
    )
    public ResponseEntity<?> deleteOrderItem(
            @PathVariable Long orderId, @PathVariable Long orderItemId) {
        orderItemService.deleteOrderItem(orderItemId);
        return ResponseEntity.noContent().build();
    }
}
