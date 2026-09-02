package com.grocery.order.service;

import com.grocery.order.dto.CustomerDTO;
import com.grocery.order.dto.LoginRequest;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    CustomerDTO login(LoginRequest loginRequest);

    CustomerDTO createCustomer(CustomerDTO customerDTO);

    CustomerDTO getCustomerById(UUID id);

    List<CustomerDTO> getAllCustomers();

    CustomerDTO updateCustomer(UUID id, CustomerDTO customerDTO);

    void deleteCustomer(UUID id);
}