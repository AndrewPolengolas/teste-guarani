package com.example.guarani.sistemas.demo.controller;

import com.example.guarani.sistemas.demo.app.dto.customer.CustomerRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.customer.CustomerResponseDTO;
import com.example.guarani.sistemas.demo.app.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO dto = customerService.createCustomer(customerRequestDTO);

        return ResponseEntity.status(201).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO dto = customerService.updateCustomer(id, customerRequestDTO);

        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO dto = customerService.getCustomerById(id);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        List<CustomerResponseDTO> dtos = customerService.getAllCustomers();
        return ResponseEntity.ok().body(dtos);
    }
}