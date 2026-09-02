CREATE TABLE categories
(
    id VARCHAR(36) PRIMARY KEY ,

    name VARCHAR(100) NOT NULL UNIQUE,

    description VARCHAR(255)
);