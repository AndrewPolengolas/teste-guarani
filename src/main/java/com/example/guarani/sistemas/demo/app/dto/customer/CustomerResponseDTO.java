package com.example.guarani.sistemas.demo.app.dto.customer;

public record CustomerResponseDTO(
        Long id,
        String name,
        String email,
        String phoneNumber
) {}
