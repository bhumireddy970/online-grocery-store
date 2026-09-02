package com.grocery.order.service;

import com.grocery.order.dto.CreateOrderRequest;
import com.grocery.order.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    List<OrderResponse> getAllOrders();

    List<OrderResponse> getOrderByCustomerId(UUID customerId);

    OrderResponse getOrder(UUID orderId);

    OrderResponse confirmOrder(UUID orderId);

    OrderResponse cancelOrder(UUID orderId);

    OrderResponse deliverOrder(UUID orderId);

}