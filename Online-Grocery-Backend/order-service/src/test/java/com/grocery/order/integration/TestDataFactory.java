package com.grocery.order.integration;

import com.grocery.order.dto.CreateOrderRequest;
import com.grocery.order.dto.OrderItemRequest;
import com.grocery.order.entity.Customer;

import java.util.List;
import java.util.UUID;

public final class TestDataFactory {

    private TestDataFactory() {

    }

    public static Customer createCustomer() {

        Customer customer = new Customer();

        customer.setName("Integration User");

        customer.setEmail("integration@test.com");

        customer.setPhone("9876543210");

        customer.setAddress("Hyderabad");

        return customer;

    }

    public static CreateOrderRequest createOrderRequest(

            UUID customerId,

            UUID productId,

            Integer quantity

    ) {

        return new CreateOrderRequest(

                customerId,

                List.of(

                        new OrderItemRequest(

                                productId,

                                quantity

                        )

                )

        );

    }

}