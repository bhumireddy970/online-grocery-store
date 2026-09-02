CREATE TABLE grocery_orders
(
    id VARCHAR(36) PRIMARY KEY ,

    customer_id VARCHAR(36) NOT NULL,

    order_date TIMESTAMP NOT NULL,

    status VARCHAR(30) NOT NULL,

    total_amount DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_order_customer
        FOREIGN KEY(customer_id)
            REFERENCES customers(id)
);