package com.grocery.order.service.impl;

import com.grocery.order.dto.CustomerDTO;
import com.grocery.order.dto.LoginRequest;
import com.grocery.order.entity.Customer;
import com.grocery.order.exception.CustomerNotFoundException;
import com.grocery.order.repository.CustomerRepository;
import com.grocery.order.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    private final CustomerRepository customerRepository;
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public CustomerDTO login(LoginRequest loginRequest) {


        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

            Customer customer = customerRepository
                    .findByEmail(loginRequest.email())
                    .orElseThrow(() ->
                            new RuntimeException("Invalid email or password")
                    );

            String token = jwtService.generateToken(customer.getEmail());
            System.out.println(token);

            return mapToDTO(customer,token);
        }catch (BadCredentialsException ex){
            throw new RuntimeException("Invalid email or password");
        }
    }



    @Override
    public CustomerDTO createCustomer(CustomerDTO dto) {

        if (customerRepository.existsByEmail(dto.email())) {
            throw new CustomerNotFoundException("Customer already exists");
        }

        Customer customer = mapToEntity(dto);
        customer.setPassword(passwordEncoder.encode(dto.password()));

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
                null,
                customer.getRole()


        );
    }

    private CustomerDTO mapToDTO(Customer customer,String token) {

        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                null,
                token,
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
        customer.setRole(dto.role() != null && !dto.role().isBlank()? dto.role() : "CUSTOMER");

        return customer;
    }
}