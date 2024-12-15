package com.example.guarani.sistemas.demo.controller;

import com.example.guarani.sistemas.demo.app.dto.customer.CustomerRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.customer.CustomerResponseDTO;
import com.example.guarani.sistemas.demo.app.service.CustomerService;
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
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "Endpoints for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_OPERATOR')")
    @Operation(
            summary = "Create a new customer",
            description = "Creates a new customer using the provided information. Requires ADMIN or OPERATOR permissions.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Customer successfully created",
                            content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    public ResponseEntity<?> createCustomer(@RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO dto = customerService.createCustomer(customerRequestDTO);
        return ResponseEntity.status(201).body(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_OPERATOR')")
    @Operation(
            summary = "Update an existing customer",
            description = "Updates the details of an existing customer identified by ID. Requires ADMIN or OPERATOR permissions.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer successfully updated",
                            content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO dto = customerService.updateCustomer(id, customerRequestDTO);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_OPERATOR')")
    @Operation(
            summary = "Get a customer by ID",
            description = "Retrieves details of a customer by their unique ID. Requires ADMIN or OPERATOR permissions.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer found",
                            content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO dto = customerService.getCustomerById(id);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_OPERATOR')")
    @Operation(
            summary = "Get all customers",
            description = "Retrieves a list of all registered customers. Requires ADMIN or OPERATOR permissions.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of customers",
                            content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class)))
            }
    )
    public ResponseEntity<?> getAllCustomers() {
        List<CustomerResponseDTO> dtos = customerService.getAllCustomers();
        return ResponseEntity.ok().body(dtos);
    }
}
