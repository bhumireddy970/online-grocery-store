package com.grocery.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.order.dto.CreateOrderRequest;
import com.grocery.order.dto.OrderItemRequest;
import com.grocery.order.dto.OrderResponse;
import com.grocery.order.entity.OrderStatus;
import com.grocery.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private OrderService orderService;

    @Test
    void createOrderShouldReturnCreated() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(orderService.createOrder(any())).thenReturn(
                new OrderResponse(orderId, customerId, OrderStatus.CREATED, LocalDateTime.now(),
                        LocalDateTime.now(), LocalDateTime.now(), BigDecimal.valueOf(120), List.of(), List.of()));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateOrderRequest(
                                customerId,
                                List.of(new OrderItemRequest(productId, 1))
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()));
    }
}
