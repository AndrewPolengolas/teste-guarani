package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.mapper.CustomerMapper;
import com.example.guarani.sistemas.demo.app.dto.customer.CustomerRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.customer.CustomerResponseDTO;
import com.example.guarani.sistemas.demo.domain.model.Customer;
import com.example.guarani.sistemas.demo.domain.repository.CustomerRepository;
import com.example.guarani.sistemas.demo.infra.exceptions.custom.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        logger.info("Creating new customer with name: {}", customerRequestDTO.name());
        Customer customer = customerMapper.toCustomer(customerRequestDTO);
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return customerMapper.toCustomerResponseDTO(savedCustomer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerRequestDTO) {
        logger.info("Updating customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Customer not found with ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });

        logger.info("Updating customer details for ID: {}", id);
        customer.setName(customerRequestDTO.name());
        customer.setEmail(customerRequestDTO.email());
        customer.setPhoneNumber(customerRequestDTO.phoneNumber());

        Customer updatedCustomer = customerRepository.save(customer);
        logger.info("Customer updated successfully with ID: {}", updatedCustomer.getId());
        return customerMapper.toCustomerResponseDTO(updatedCustomer);
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        logger.info("Fetching customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Customer not found with ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });

        logger.info("Customer fetched successfully with ID: {}", id);
        return customerMapper.toCustomerResponseDTO(customer);
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        logger.info("Fetching all customers");
        List<Customer> customers = customerRepository.findAll();
        logger.info("Fetched {} customers", customers.size());
        return customers.stream()
                .map(customerMapper::toCustomerResponseDTO)
                .toList();
    }
}
