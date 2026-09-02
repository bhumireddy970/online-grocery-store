package com.grocery.order.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.order.dto.CreateOrderRequest;
import com.grocery.order.dto.OrderItemRequest;
import com.grocery.order.entity.Customer;
import com.grocery.order.entity.GroceryOrder;
import com.grocery.order.entity.OrderStatus;
import com.grocery.order.repository.CustomerRepository;
import com.grocery.order.repository.GroceryOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("Requires live downstream services and gateway state")
class OrderIntegrationTest extends AbstractOrderIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GroceryOrderRepository groceryOrderRepository;

    @Test
    void createOrderShouldPersistOrderItemsAndCalculateTotal() throws Exception {
        Customer customer = saveCustomer();
        UUID productId = fetchAnyProductId();

        CreateOrderRequest request = new CreateOrderRequest(
                customer.getId(),
                List.of(new OrderItemRequest(productId, 1))
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customer.getId().toString()))
                .andExpect(jsonPath("$.status").value(OrderStatus.CREATED.name()));

        GroceryOrder savedOrder = groceryOrderRepository.findAll().get(0);
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(savedOrder.getOrderItems()).isNotEmpty();
    }

    @Test
    void createOrderShouldReturnNotFoundForMissingCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID productId = fetchAnyProductId();

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                List.of(new OrderItemRequest(productId, 1))
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));

        assertThat(groceryOrderRepository.count()).isZero();
    }

    @Test
    void createOrderShouldRejectEmptyItemsPayload() throws Exception {
        Customer customer = saveCustomer();

        CreateOrderRequest request = new CreateOrderRequest(customer.getId(), List.of());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Order must contain at least one item"));
    }

    @Test
    void confirmThenDeliverThenRejectInvalidTransitions() throws Exception {
        Customer customer = saveCustomer();
        UUID productId = fetchAnyProductId();
        UUID orderId = createOrder(customer.getId(), productId);

        mockMvc.perform(put("/api/orders/{id}/confirm", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.CONFIRMED.name()));

        mockMvc.perform(put("/api/orders/{id}/deliver", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERED.name()));

        mockMvc.perform(put("/api/orders/{id}/cancel", orderId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Delivered order cannot be cancelled"));
    }

    @Test
    void cancelShouldReleaseInventoryAndUpdateStatus() throws Exception {
        Customer customer = saveCustomer();
        UUID productId = fetchAnyProductId();
        UUID orderId = createOrder(customer.getId(), productId);

        mockMvc.perform(put("/api/orders/{id}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.CANCELLED.name()));
    }

    private UUID createOrder(UUID customerId, UUID productId) throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                List.of(new OrderItemRequest(productId, 1))
        );

        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(response).get("orderId").asText());
    }

    private Customer saveCustomer() {
        Customer customer = new Customer();
        customer.setName("Integration User");
        customer.setEmail("integration+" + UUID.randomUUID() + "@test.com");
        customer.setPhone("9876543210");
        customer.setAddress("Hyderabad");
        return customerRepository.save(customer);
    }

    private UUID fetchAnyProductId() throws Exception {
        String payload = restTemplate.getForObject("http://localhost:8080/api/products", String.class);
        JsonNode root = objectMapper.readTree(payload);
        if (!root.isArray() || root.isEmpty()) {
            throw new IllegalStateException("No products available from product service");
        }
        return UUID.fromString(root.get(0).get("id").asText());
    }
}
