package com.grocery.order.service.impl;

import com.grocery.order.dto.CustomerDTO;
import com.grocery.order.dto.LoginRequest;
import com.grocery.order.entity.Customer;
import com.grocery.order.exception.CustomerNotFoundException;
import com.grocery.order.repository.CustomerRepository;
import com.grocery.order.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerDTO login(LoginRequest loginRequest) {

        Customer customer = customerRepository
                .findByEmail(loginRequest.email())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        if (!customer.getPassword()
                .equals(loginRequest.password())) {

            throw new RuntimeException("Invalid email or password");
        }

        return mapToDTO(customer);
    }

    @Override
    public CustomerDTO createCustomer(CustomerDTO dto) {

        if (customerRepository.existsByEmail(dto.email())) {
            throw new CustomerNotFoundException("Customer already exists");
        }

        Customer customer = mapToEntity(dto);

        Customer saved = customerRepository.save(customer);

        return mapToDTO(saved);
    }

    @Override
    public CustomerDTO getCustomerById(UUID id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        return mapToDTO(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public CustomerDTO updateCustomer(UUID id, CustomerDTO dto) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhone(dto.phone());
        customer.setAddress(dto.address());

        Customer updated = customerRepository.save(customer);

        return mapToDTO(updated);
    }

    @Override
    public void deleteCustomer(UUID id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        customerRepository.delete(customer);
    }



    private CustomerDTO mapToDTO(Customer customer) {

        return new CustomerDTO(

                customer.getId(),

                customer.getName(),

                customer.getEmail(),

                customer.getPhone(),

                customer.getAddress(),
                null,
                customer.getRole()


        );
    }

    private Customer mapToEntity(CustomerDTO dto) {

        Customer customer = new Customer();

        customer.setId(dto.id());
        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhone(dto.phone());
        customer.setAddress(dto.address());
        customer.setPassword(dto.password());

        return customer;
    }
}