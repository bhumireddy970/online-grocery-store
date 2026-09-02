package com.grocery.order.repository;

import com.grocery.order.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired private CustomerRepository customerRepository;

    @Test
    void shouldSaveAndFindCustomerByEmail() {
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail("user-" + UUID.randomUUID() + "@test.com");
        customer.setPhone("1234567890");
        customer.setAddress("Hyderabad");

        Customer saved = customerRepository.save(customer);

        assertThat(customerRepository.existsByEmail(saved.getEmail())).isTrue();
        assertThat(customerRepository.findByEmail(saved.getEmail())).contains(saved);
    }
}
