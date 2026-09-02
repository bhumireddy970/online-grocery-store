package com.grocery.order.service.impl;

import com.grocery.order.client.InventoryClient;
import com.grocery.order.client.ProductClient;
import com.grocery.order.dto.CreateOrderRequest;
import com.grocery.order.dto.OrderItemRequest;
import com.grocery.order.dto.OrderResponse;
import com.grocery.order.dto.external.*;
import com.grocery.order.entity.Customer;
import com.grocery.order.entity.GroceryOrder;
import com.grocery.order.entity.OrderItem;
import com.grocery.order.entity.OrderStatus;
import com.grocery.order.exception.CustomerNotFoundException;
import com.grocery.order.exception.InsufficientStockException;
import com.grocery.order.exception.InvalidOrderStatusException;
import com.grocery.order.exception.OrderNotFoundException;
import com.grocery.order.repository.CustomerRepository;
import com.grocery.order.repository.GroceryOrderRepository;
import com.grocery.order.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private GroceryOrderRepository groceryOrderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private ProductClient productClient;
    @Mock private InventoryClient inventoryClient;
    @Mock private com.grocery.order.service.communication.ProductCommunicationService productCommunicationService;
    @Mock private com.grocery.order.service.communication.InventoryCommunicationService inventoryCommunicationService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID customerId;
    private UUID productId;
    private Customer customer;
    private GroceryOrder order;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        productId = UUID.randomUUID();

        customer = new Customer();
        customer.setId(customerId);
        customer.setName("Test");
        customer.setEmail("test@test.com");

        order = new GroceryOrder();
        order.setId(UUID.randomUUID());
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(BigDecimal.valueOf(120));

        OrderItem item = new OrderItem();
        item.setId(UUID.randomUUID());
        item.setGroceryOrder(order);
        item.setProductId(productId);
        item.setProductName("Apple");
        item.setPrice(BigDecimal.valueOf(120));
        item.setQuantity(1);
        item.setSubTotal(BigDecimal.valueOf(120));
        order.setOrderItems(List.of(item));
    }

    @Test
    void createOrderShouldCreateOrder() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productCommunicationService.getProducts(any())).thenReturn(List.of(
                new ProductResponse(productId, "APL", "Apple", "desc", BigDecimal.valueOf(120), true,
                        UUID.randomUUID(), "Fruits", LocalDateTime.now(), LocalDateTime.now())));
        when(inventoryCommunicationService.reserveBulkInventory(any())).thenReturn(new BulkReserveInventoryResponse(List.of(new ReservedInventoryItem(productId, 1)), List.of()));
        when(groceryOrderRepository.save(any(GroceryOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.createOrder(new CreateOrderRequest(customerId, List.of(new OrderItemRequest(productId, 1))));

        assertThat(response.status()).isEqualTo(OrderStatus.CREATED);
        verify(groceryOrderRepository).save(any(GroceryOrder.class));
    }

    @Test
    void createOrderShouldThrowWhenCustomerMissing() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(new CreateOrderRequest(customerId, List.of(new OrderItemRequest(productId, 1)))))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void createOrderShouldThrowWhenNothingReserved() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productCommunicationService.getProducts(any())).thenReturn(List.of(
                new ProductResponse(productId, "APL", "Apple", "desc", BigDecimal.valueOf(120), true,
                        UUID.randomUUID(), "Fruits", LocalDateTime.now(), LocalDateTime.now())));
        when(inventoryCommunicationService.reserveBulkInventory(any())).thenReturn(new BulkReserveInventoryResponse(List.of(), List.of()));

        assertThatThrownBy(() -> orderService.createOrder(new CreateOrderRequest(customerId, List.of(new OrderItemRequest(productId, 1)))))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void confirmOrderShouldUpdateStatus() {
        when(groceryOrderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(groceryOrderRepository.save(any(GroceryOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.confirmOrder(order.getId());

        assertThat(response.status()).isEqualTo(OrderStatus.CONFIRMED);
        verify(inventoryCommunicationService).confirmInventory(any());
    }

    @Test
    void cancelOrderShouldReleaseInventory() {
        when(groceryOrderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(groceryOrderRepository.save(any(GroceryOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.cancelOrder(order.getId());

        assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);
        verify(inventoryCommunicationService).releaseInventory(any());
    }

    @Test
    void deliverOrderShouldValidateStatus() {
        order.setStatus(OrderStatus.CONFIRMED);
        when(groceryOrderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(groceryOrderRepository.save(any(GroceryOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.deliverOrder(order.getId());

        assertThat(response.status()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void getOrderShouldThrowWhenMissing() {
        when(groceryOrderRepository.findById(order.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.getOrder(order.getId())).isInstanceOf(OrderNotFoundException.class);
    }
}
