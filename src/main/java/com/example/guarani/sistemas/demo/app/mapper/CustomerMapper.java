package com.example.guarani.sistemas.demo.app.mapper;

import com.example.guarani.sistemas.demo.app.dto.customer.CustomerRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.customer.CustomerResponseDTO;
import com.example.guarani.sistemas.demo.domain.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = new Customer();
        customer.setName(customerRequestDTO.name());
        customer.setEmail(customerRequestDTO.email());
        customer.setPhoneNumber(customerRequestDTO.phoneNumber());
        return customer;
    }

    public CustomerResponseDTO toCustomerResponseDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhoneNumber()
        );
    }
}