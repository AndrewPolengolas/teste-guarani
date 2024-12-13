package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.mapper.CustomerMapper;
import com.example.guarani.sistemas.demo.app.dto.customer.CustomerRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.customer.CustomerResponseDTO;
import com.example.guarani.sistemas.demo.domain.model.Customer;
import com.example.guarani.sistemas.demo.domain.repository.CustomerRepository;
import com.example.guarani.sistemas.demo.infra.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = customerMapper.toCustomer(customerRequestDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toCustomerResponseDTO(savedCustomer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerRequestDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setName(customerRequestDTO.name());
        customer.setEmail(customerRequestDTO.email());
        customer.setPhoneNumber(customerRequestDTO.phoneNumber());

        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toCustomerResponseDTO(updatedCustomer);
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toCustomerResponseDTO(customer);
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customerMapper::toCustomerResponseDTO)
                .toList();
    }
}