package com.example.guarani.sistemas.demo.app.service;


import com.example.guarani.sistemas.demo.app.dto.customer.CustomerRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.customer.CustomerResponseDTO;
import com.example.guarani.sistemas.demo.app.mapper.CustomerMapper;
import com.example.guarani.sistemas.demo.domain.model.Customer;
import com.example.guarani.sistemas.demo.domain.repository.CustomerRepository;
import com.example.guarani.sistemas.demo.infra.exceptions.custom.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCustomer() {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO("John Doe", "john@example.com", "123456789");
        Customer customer = new Customer(null, "John Doe", "john@example.com", "123456789", null);
        Customer savedCustomer = new Customer(1L, "John Doe", "john@example.com", "123456789", null);
        CustomerResponseDTO responseDTO = new CustomerResponseDTO(1L, "John Doe", "john@example.com", "123456789");

        when(customerMapper.toCustomer(requestDTO)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(savedCustomer);
        when(customerMapper.toCustomerResponseDTO(savedCustomer)).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.createCustomer(requestDTO);

        assertEquals(responseDTO, result);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testUpdateCustomer() {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO("Jane Doe", "jane@example.com", "987654321");
        Customer existingCustomer = new Customer(1L, "John Doe", "john@example.com", "123456789", null);
        Customer updatedCustomer = new Customer(1L, "Jane Doe", "jane@example.com", "987654321", null);
        CustomerResponseDTO responseDTO = new CustomerResponseDTO(1L, "Jane Doe", "jane@example.com", "987654321");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(existingCustomer)).thenReturn(updatedCustomer);
        when(customerMapper.toCustomerResponseDTO(updatedCustomer)).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.updateCustomer(1L, requestDTO);

        assertEquals(responseDTO, result);
        assertEquals("Jane Doe", existingCustomer.getName());
        verify(customerRepository, times(1)).save(existingCustomer);
    }

    @Test
    void testGetCustomerById() {
        Customer customer = new Customer(1L, "John Doe", "john@example.com", "123456789", null);
        CustomerResponseDTO responseDTO = new CustomerResponseDTO(1L, "John Doe", "john@example.com", "123456789");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toCustomerResponseDTO(customer)).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.getCustomerById(1L);

        assertEquals(responseDTO, result);
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCustomerByIdNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(1L));
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllCustomers() {
        Customer customer1 = new Customer(1L, "John Doe", "john@example.com", "123456789", null);
        Customer customer2 = new Customer(2L, "Jane Doe", "jane@example.com", "987654321", null);
        CustomerResponseDTO responseDTO1 = new CustomerResponseDTO(1L, "John Doe", "john@example.com", "123456789");
        CustomerResponseDTO responseDTO2 = new CustomerResponseDTO(2L, "Jane Doe", "jane@example.com", "987654321");

        when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));
        when(customerMapper.toCustomerResponseDTO(customer1)).thenReturn(responseDTO1);
        when(customerMapper.toCustomerResponseDTO(customer2)).thenReturn(responseDTO2);

        List<CustomerResponseDTO> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        assertTrue(result.contains(responseDTO1));
        assertTrue(result.contains(responseDTO2));
        verify(customerRepository, times(1)).findAll();
    }
}
