CREATE TABLE inventory
(
    id VARCHAR(36) PRIMARY KEY ,

    product_id VARCHAR(36) NOT NULL UNIQUE,

    available_quantity INT NOT NULL,

    reserved_quantity INT NOT NULL
);