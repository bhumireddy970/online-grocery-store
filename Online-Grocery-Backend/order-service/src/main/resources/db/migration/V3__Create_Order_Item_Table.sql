CREATE TABLE order_items
(
    id VARCHAR(36) PRIMARY KEY ,

    order_id VARCHAR(36) NOT NULL,

    product_id VARCHAR(36) NOT NULL,

    product_name VARCHAR(150) NOT NULL,

    price DECIMAL(10,2) NOT NULL,

    quantity INT NOT NULL,

    sub_total DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_order_item
        FOREIGN KEY(order_id)
            REFERENCES grocery_orders(id)
);