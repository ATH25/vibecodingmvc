-- Flyway migration: initial schema for beers and beer orders
-- H2-compatible DDL

CREATE TABLE IF NOT EXISTS beer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    version INT,
    beer_name VARCHAR(255),
    beer_style VARCHAR(100),
    upc VARCHAR(32),
    quantity_on_hand INT,
    price DECIMAL(19,2),
    created_date TIMESTAMP,
    updated_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS beer_order (
    id INT AUTO_INCREMENT PRIMARY KEY,
    version INT,
    customer_ref VARCHAR(255),
    payment_amount DECIMAL(19,2),
    status VARCHAR(50),
    created_date TIMESTAMP,
    updated_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS beer_order_line (
    id INT AUTO_INCREMENT PRIMARY KEY,
    version INT,
    beer_order_id INT NOT NULL,
    beer_id INT NOT NULL,
    order_quantity INT,
    quantity_allocated INT,
    status VARCHAR(50),
    created_date TIMESTAMP,
    updated_date TIMESTAMP,
    CONSTRAINT fk_bol_order FOREIGN KEY (beer_order_id) REFERENCES beer_order(id),
    CONSTRAINT fk_bol_beer FOREIGN KEY (beer_id) REFERENCES beer(id)
);

CREATE INDEX IF NOT EXISTS idx_bol_order ON beer_order_line (beer_order_id);
CREATE INDEX IF NOT EXISTS idx_bol_beer ON beer_order_line (beer_id);
