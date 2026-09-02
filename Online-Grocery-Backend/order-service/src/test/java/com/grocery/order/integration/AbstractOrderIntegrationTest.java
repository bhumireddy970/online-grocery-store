package com.grocery.order.integration;

import com.grocery.order.repository.CustomerRepository;
import com.grocery.order.repository.GroceryOrderRepository;
import com.grocery.order.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractOrderIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected GroceryOrderRepository groceryOrderRepository;

    @Autowired
    protected OrderItemRepository orderItemRepository;

    @BeforeEach
    void cleanDatabase() {
        orderItemRepository.deleteAll();
        groceryOrderRepository.deleteAll();
        customerRepository.deleteAll();
    }
}
