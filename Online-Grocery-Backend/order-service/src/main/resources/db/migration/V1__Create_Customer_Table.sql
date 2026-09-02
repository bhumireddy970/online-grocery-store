CREATE TABLE customers
(
    id VARCHAR(36) PRIMARY KEY ,

    name VARCHAR(100) NOT NULL,

    email VARCHAR(100) NOT NULL UNIQUE,

    phone VARCHAR(20),

    address VARCHAR(255)
);