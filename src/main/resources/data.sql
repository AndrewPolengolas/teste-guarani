INSERT IGNORE INTO roles(role_id, name) VALUES
(1, 'ADMIN'),
(2, 'OPERATOR');

INSERT IGNORE INTO customer (id, name, email, phone_number) VALUES
(1, 'Darcy Emilia', 'darcy.emilia@gmail.com', '1234567890'),
(2, 'Igor Nascimento', 'igor.nascimento@gmail.com', '0987654321');

INSERT IGNORE INTO product (id, name, description, price, category, stock_quantity) VALUES
(1, 'Laptop', 'High-performance laptop', 1200.00, 'ELECTRONICS', 50),
(2, 'Smartphone', 'Latest model smartphone', 800.00, 'ELECTRONICS', 100),
(3, 'Headphones', 'Noise-cancelling headphones', 150.00, 'ELECTRONICS', 200);

INSERT IGNORE INTO customer_order (id, creation_date, total_amount, status, customer_id, payment_status, payment_date, discount, shipping_fee) VALUES
(1, '2024-12-01', 1400.00, 'OPEN', 1, 'PENDING', NULL, 0.10, 50.00),
(2, '2024-12-05', 800.00, 'CLOSED', 2, 'COMPLETED', '2024-12-06', 0.05, 20.00);

INSERT IGNORE INTO order_item (id, order_id, product_id, quantity, total_price) VALUES
(1, 1, 1, 1, 1200.00),
(2, 1, 3, 2, 300.00),
(3, 2, 2, 1, 800.00);

INSERT IGNORE INTO customer (id, name, email, phone_number) VALUES
(3, 'Ana Vitoria', 'ana.vitoria@gmail.com', '1122334455'),
(4, 'Andrew do Nascimento', 'andrew.nascimento@gmail.com', '2233445566');

INSERT IGNORE INTO customer_order (id, creation_date, total_amount, status, customer_id, payment_status, payment_date, discount, shipping_fee) VALUES 
(3, '2024-12-10', 450.00, 'OPEN', 3, 'PENDING', NULL, 0.15, 10.00);

INSERT IGNORE INTO order_item (id, order_id, product_id, quantity, total_price) VALUES 
(4, 3, 3, 3, 450.00);
