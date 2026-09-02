# Online Grocery Store Backend

## Overview

This project is a backend implementation of an Online Grocery Store developed using Spring Boot Microservices. The application is divided into multiple independent services where each service is responsible for its own business logic and database.

The project was developed as part of a Spring Boot Microservices coding assessment. The focus is on clean architecture, REST APIs, database design, validation, exception handling, testing, and communication between microservices.

Payment functionality is intentionally kept out of scope as mentioned in the assessment.

---

## Technologies Used

- Java 17
- Spring Boot 3
- Spring Cloud
- Maven
- MySQL
- Spring Data JPA
- Hibernate
- OpenFeign
- Eureka Server
- Spring Validation
- Flyway
- Swagger (OpenAPI)
- Resilience4j
- JUnit 5
- Mockito
- MockMvc
- Lombok
- Postman

---

## Project Structure

The project contains the following microservices.

```
Online-Grocery-Store

─ service-registry
─ api-gateway
─ product-service
─ inventory-service
─ order-service
```

### Service Registry

Acts as the Eureka Server where all services register themselves.

### API Gateway

Provides a single entry point for all client requests and routes them to the appropriate microservice.

### Product Service

Responsible for managing categories and products.

Database:
- product_catalog_db

Tables:
- categories
- products

### Inventory Service

Responsible for maintaining product stock and inventory operations.

Database:
- inventory_db

Tables:
- inventory
- stock_movements

### Order Service

Responsible for creating and managing customer orders.

Database:
- order_db

Tables:
- customers
- grocery_orders
- order_items

Each microservice owns its own database schema. No foreign key relationships are created across different databases. Services communicate using IDs.

---

## Features

### Product Service

- Create Category
- View Categories
- Create Product
- Update Product
- Delete Product
- View Products
- Product Validation

### Inventory Service

- Add Inventory
- View Inventory
- Reserve Stock
- Release Stock
- Confirm Stock

### Order Service

- Create Order
- Get All Orders
- Get Order by Id
- Confirm Order
- Cancel Order
- Deliver Order

---

## Business Rules

The following business rules are implemented.

### Product

- Product price must be greater than zero.
- Product must belong to an existing category.
- Duplicate SKU is not allowed.
- Inactive products cannot be ordered.

### Inventory

- Inventory must exist before placing an order.
- Available quantity cannot be negative.
- Reserved quantity cannot be negative.
- Stock is reserved when an order is created.
- Reserved stock is released when an order is cancelled.
- Reserved stock is deducted when an order is confirmed.

### Order

- An order must contain at least one item.
- Quantity must be greater than zero.
- Order total is calculated in the backend.
- Product information is fetched from Product Service.
- Inventory is validated through Inventory Service.
- Product name and price are stored as snapshots in the order.
- Delivered orders cannot be cancelled.
- Cancelled orders cannot be delivered.

---

## Service Communication

The Order Service communicates with the following services using OpenFeign.

- Product Service
- Inventory Service

Order creation flow:

```
Client

|

Order Service

|

Validate Customer

|

Get Product Details

|

Check Inventory

|

Reserve Inventory

|

Create Order

|

Return Response
```

---

## Exception Handling

Global exception handling is implemented using `@ControllerAdvice`.

Handled exceptions include:

- Product Not Found
- Category Not Found
- Customer Not Found
- Inventory Not Found
- Duplicate SKU
- Insufficient Stock
- Invalid Order Status
- Invalid Request Payload

---

## Validation

Bean Validation is used throughout the application.

Some commonly used validations include:

- @NotNull
- @NotBlank
- @Positive
- @Valid

---

## API Documentation

Swagger UI is enabled for every microservice.

Example:

```
http://localhost:8081/swagger-ui.html
```

---

## Database Migration

Flyway is used to manage database schema changes.

Migration scripts are available under:

```
src/main/resources/db/migration
```

---

## Testing

The project includes the following tests.

- Unit Tests
- Repository Tests
- Controller Tests
- Integration Tests

Integration tests use:

- Local MySQL Database
- MockMvc
- MockitoBean
- Mocked Feign Clients

Test cases cover:

- Order Creation
- Retrieve Orders
- Retrieve Order by Id
- Confirm Order
- Cancel Order
- Deliver Order
- Customer Not Found
- Product Not Found
- Inactive Product
- Insufficient Stock
- Invalid Order Status Transition

---


## Author

**Bhumireddy Saradhi**

Coding Assessment – Online Grocery Store Backend using Spring Boot Microservices