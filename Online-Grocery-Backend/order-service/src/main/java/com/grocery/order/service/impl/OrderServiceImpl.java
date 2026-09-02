package com.grocery.order.service.impl;

import com.grocery.order.client.InventoryClient;
import com.grocery.order.client.ProductClient;
import com.grocery.order.dto.*;
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
import com.grocery.order.service.OrderService;
import com.grocery.order.service.communication.InventoryCommunicationService;
import com.grocery.order.service.communication.ProductCommunicationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CustomerRepository customerRepository;

    private final GroceryOrderRepository groceryOrderRepository;

    private final OrderItemRepository orderItemRepository;

    private final ProductClient productClient;

    private final InventoryClient inventoryClient;

    private final ProductCommunicationService productCommunicationService;

    private final InventoryCommunicationService inventoryCommunicationService;


    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found"));

        GroceryOrder order = new GroceryOrder();

        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);

        List<OrderItem> orderItems = new ArrayList<>();

        List<SkippedOrderItemResponse> skippedItems = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;


        BulkProductRequest bulkProductRequest =
                new BulkProductRequest(

                        request.items()
                                .stream()
                                .map(OrderItemRequest::productId)
                                .toList()

                );

        List<ProductResponse> products = productCommunicationService.getProducts(bulkProductRequest);

        Map<UUID, ProductResponse> productMap =
                products.stream()
                        .collect(Collectors.toMap(
                                ProductResponse::id,
                                Function.identity()));


        List<ReserveInventoryRequest> reserveRequests =
                request.items()
                        .stream()
                        .map(item -> new ReserveInventoryRequest(
                                item.productId(),
                                item.quantity()))
                        .toList();

        BulkReserveInventoryResponse reserveResponse =
                inventoryCommunicationService.reserveBulkInventory(

                        new BulkReserveInventoryRequest(
                                reserveRequests)

                );

        Map<UUID, ReservedInventoryItem> reservedMap =
                reserveResponse.reservedItems()
                        .stream()
                        .collect(Collectors.toMap(
                                ReservedInventoryItem::productId,
                                Function.identity()));

        try {
            for (OrderItemRequest itemRequest : request.items()) {

                ReservedInventoryItem reservedItem =
                        reservedMap.get(itemRequest.productId());

                if (reservedItem == null) {
                    continue;
                }

                ProductResponse product =
                        productMap.get(itemRequest.productId());

                if (product == null) {
                    continue;
                }

                if (!product.active()) {
                    continue;
                }

                BigDecimal subTotal =
                        product.price().multiply(
                                BigDecimal.valueOf(
                                        reservedItem.quantity()));

                totalAmount = totalAmount.add(subTotal);

                OrderItem orderItem = new OrderItem();

                orderItem.setGroceryOrder(order);

                orderItem.setProductId(product.id());

                orderItem.setProductName(product.name());

                orderItem.setPrice(product.price());

                orderItem.setQuantity(reservedItem.quantity());

                orderItem.setSubTotal(subTotal);

                orderItems.add(orderItem);
            }

            for (SkippedInventoryItem skipped :
                    reserveResponse.skippedItems()) {

                ProductResponse product =
                        productMap.get(skipped.productId());

                skippedItems.add(

                        new SkippedOrderItemResponse(

                                skipped.productId(),

                                product == null
                                        ? "Unknown Product"
                                        : product.name(),

                                skipped.requestedQuantity(),

                                skipped.availableQuantity(),

                                skipped.reason()

                        )

                );

            }
            if (orderItems.isEmpty()) {

                throw new InsufficientStockException(

                        "None of the requested products are available."

                );

            }

            order.setTotalAmount(totalAmount);

            order.setOrderItems(orderItems);

            GroceryOrder savedOrder =
                    groceryOrderRepository.save(order);

            OrderResponse response = mapToResponse(savedOrder);

            return new OrderResponse(

                    response.orderId(),

                    response.customerId(),

                    response.status(),

                    response.orderDate(),

                    response.createdAt(),

                    response.updatedAt(),

                    response.totalAmount(),

                    response.items(),

                    skippedItems

            );

        } catch (Exception ex) {

            // Release ONLY successfully reserved products

            if (!reserveResponse.reservedItems().isEmpty()) {

                List<ReleaseInventoryRequest> releaseRequests =
                        reserveResponse.reservedItems()
                                .stream()
                                .map(item ->

                                        new ReleaseInventoryRequest(

                                                item.productId(),

                                                item.quantity()

                                        )

                                )
                                .toList();

                try {

                    inventoryCommunicationService.releaseBulkInventory(

                            new BulkReleaseInventoryRequest(

                                    releaseRequests

                            )

                    );

                } catch (Exception ignored) {

                }

            }

            throw ex;

        }

    }

    @Override
    public List<OrderResponse> getAllOrders() {

        return groceryOrderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

    }

    @Override
    public List<OrderResponse> getOrderByCustomerId(UUID customerId) {
        return groceryOrderRepository.findAll()
                .stream()
                .filter(order -> order.getCustomer().getId().equals(customerId))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrder(UUID orderId) {

        GroceryOrder order = groceryOrderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found"));

        return mapToResponse(order);

    }

    @Override
    public OrderResponse confirmOrder(UUID orderId) {

        GroceryOrder order = groceryOrderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderStatusException(
                    "Only CREATED orders can be confirmed");
        }

        for (OrderItem item : order.getOrderItems()) {

            inventoryCommunicationService.confirmInventory(

                    new ConfirmInventoryRequest(

                            item.getProductId(),

                            item.getQuantity()

                    )

            );

        }

        order.setStatus(OrderStatus.CONFIRMED);

        GroceryOrder updatedOrder =
                groceryOrderRepository.save(order);

        return mapToResponse(updatedOrder);

    }

    @Override
    public OrderResponse cancelOrder(UUID orderId) {

        GroceryOrder order = groceryOrderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusException(
                    "Delivered order cannot be cancelled");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(
                    "Order already cancelled");
        }

        for (OrderItem item : order.getOrderItems()) {

            ReleaseInventoryRequest request =
                    new ReleaseInventoryRequest(
                            item.getProductId(),
                            item.getQuantity());

            inventoryCommunicationService.releaseInventory(request);
        }

        order.setStatus(OrderStatus.CANCELLED);

        GroceryOrder updatedOrder =
                groceryOrderRepository.save(order);

        return mapToResponse(updatedOrder);

    }

    @Override
    public OrderResponse deliverOrder(UUID orderId) {

        GroceryOrder order = groceryOrderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(
                    "Cancelled order cannot be delivered");
        }

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusException(
                    "Only confirmed orders can be delivered");
        }

        order.setStatus(OrderStatus.DELIVERED);

        GroceryOrder updatedOrder =
                groceryOrderRepository.save(order);

        return mapToResponse(updatedOrder);

    }

    private OrderResponse mapToResponse(GroceryOrder order) {

        List<OrderItemResponse> items =
                order.getOrderItems()
                        .stream()
                        .map(item ->

                                new OrderItemResponse(

                                        item.getProductId(),

                                        item.getProductName(),

                                        item.getPrice(),

                                        item.getQuantity(),

                                        item.getSubTotal()

                                )

                        )
                        .toList();

        return new OrderResponse(

                order.getId(),

                order.getCustomer().getId(),

                order.getStatus(),

                order.getOrderDate(),

                order.getCreatedAt(),

                order.getUpdatedAt(),

                order.getTotalAmount(),

                items,

                List.of()

        );

    }

}


