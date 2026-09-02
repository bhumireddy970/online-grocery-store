CREATE TABLE products
(
    id VARCHAR(36) PRIMARY KEY,

    sku VARCHAR(50) NOT NULL UNIQUE,

    name VARCHAR(150) NOT NULL,

    description VARCHAR(255),

    price DECIMAL(10,2) NOT NULL,

    active BOOLEAN NOT NULL,

    category_id VARCHAR(36) NOT NULL,

    CONSTRAINT fk_product_category
        FOREIGN KEY(category_id)
            REFERENCES categories(id)
);