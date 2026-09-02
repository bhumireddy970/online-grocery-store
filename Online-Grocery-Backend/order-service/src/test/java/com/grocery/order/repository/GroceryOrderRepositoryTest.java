package com.grocery.order.repository;

import com.grocery.order.entity.Customer;
import com.grocery.order.entity.GroceryOrder;
import com.grocery.order.entity.OrderItem;
import com.grocery.order.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GroceryOrderRepositoryTest {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private GroceryOrderRepository groceryOrderRepository;

    @Test
    void shouldPersistOrderWithItems() {
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail("order-" + UUID.randomUUID() + "@test.com");
        customer.setPhone("1234567890");
        customer.setAddress("Hyderabad");
        customer = customerRepository.save(customer);

        GroceryOrder order = new GroceryOrder();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(BigDecimal.valueOf(120));

        OrderItem item = new OrderItem();
        item.setGroceryOrder(order);
        item.setProductId(UUID.randomUUID());
        item.setProductName("Apple");
        item.setPrice(BigDecimal.valueOf(120));
        item.setQuantity(1);
        item.setSubTotal(BigDecimal.valueOf(120));
        order.setOrderItems(List.of(item));

        GroceryOrder saved = groceryOrderRepository.save(order);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getOrderItems()).hasSize(1);
    }
}
