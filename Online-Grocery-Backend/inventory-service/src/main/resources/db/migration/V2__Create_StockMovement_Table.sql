CREATE TABLE stock_movements
(
    id VARCHAR(36) PRIMARY KEY ,

    product_id VARCHAR(36) NOT NULL,

    quantity INT NOT NULL,

    movement_type VARCHAR(20) NOT NULL,

    movement_date TIMESTAMP NOT NULL
);