package com.example.guarani.sistemas.demo.app.dto.customer;

public record CustomerRequestDTO(
        String name,
        String email,
        String phoneNumber
) {}