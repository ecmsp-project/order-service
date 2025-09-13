-- V2__Insert_example_data.sql
-- Flyway migration to insert example data into orders and order_item tables

-- Insert example orders
INSERT INTO orders (order_id, order_status, date, client_id) VALUES
                                                                 ('550e8400-e29b-41d4-a716-446655440001', 'PENDING', '2024-01-15 10:30:00', '123e4567-e89b-12d3-a456-426614174001'),
                                                                 ('550e8400-e29b-41d4-a716-446655440002', 'PAID', '2024-01-16 14:22:15', '123e4567-e89b-12d3-a456-426614174002'),
                                                                 ('550e8400-e29b-41d4-a716-446655440003', 'PROCESSING', '2024-01-17 09:45:30', '123e4567-e89b-12d3-a456-426614174001'),
                                                                 ('550e8400-e29b-41d4-a716-446655440004', 'CANCELLED', '2024-01-18 16:10:45', '123e4567-e89b-12d3-a456-426614174003'),
                                                                 ('550e8400-e29b-41d4-a716-446655440005', 'PROCESSING', '2024-01-19 11:20:00', '123e4567-e89b-12d3-a456-426614174002'),
                                                                 ('550e8400-e29b-41d4-a716-446655440006', 'PAID', '2024-01-20 13:55:12', '123e4567-e89b-12d3-a456-426614174004'),
                                                                 ('550e8400-e29b-41d4-a716-446655440007', 'PENDING', '2024-01-21 08:15:30', '123e4567-e89b-12d3-a456-426614174001'),
                                                                 ('550e8400-e29b-41d4-a716-446655440008', 'FAILED', '2024-01-22 15:40:25', '123e4567-e89b-12d3-a456-426614174005');

-- Insert example order items
INSERT INTO order_item (item_id, order_id, item_name, quantity, price, description) VALUES
                                                                                        -- Order 1 items
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', '550e8400-e29b-41d4-a716-446655440001', 'Wireless Headphones', 2, 129.99, 'Premium noise-cancelling wireless headphones'),
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef1234567891', '550e8400-e29b-41d4-a716-446655440001', 'Phone Case', 1, 24.99, 'Protective silicone phone case'),

                                                                                        -- Order 2 items
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef1234567892', '550e8400-e29b-41d4-a716-446655440002', 'Laptop Stand', 1, 89.99, 'Adjustable aluminum laptop stand'),
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef1234567893', '550e8400-e29b-41d4-a716-446655440002', 'USB-C Cable', 3, 19.99, '6ft USB-C charging cable'),
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef1234567894', '550e8400-e29b-41d4-a716-446655440002', 'Wireless Mouse', 1, 45.99, 'Ergonomic wireless optical mouse'),

                                                                                        -- Order 3 items
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef1234567895', '550e8400-e29b-41d4-a716-446655440003', 'Monitor', 1, 299.99, '27-inch 4K LED monitor'),
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef1234567896', '550e8400-e29b-41d4-a716-446655440003', 'Keyboard', 1, 79.99, 'Mechanical gaming keyboard'),

                                                                                        -- Order 4 items (cancelled order)
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef1234567897', '550e8400-e29b-41d4-a716-446655440004', 'Webcam', 1, 149.99, 'HD webcam with auto-focus'),

                                                                                        -- Order 5 items
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef1234567898', '550e8400-e29b-41d4-a716-446655440005', 'Tablet', 1, 399.99, '10-inch Android tablet'),
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef1234567899', '550e8400-e29b-41d4-a716-446655440005', 'Tablet Case', 1, 34.99, 'Leather tablet case with stand'),
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef123456789a', '550e8400-e29b-41d4-a716-446655440005', 'Screen Protector', 2, 12.99, 'Tempered glass screen protector'),

                                                                                        -- Order 6 items
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef123456789b', '550e8400-e29b-41d4-a716-446655440006', 'Bluetooth Speaker', 1, 79.99, 'Portable waterproof Bluetooth speaker'),
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef123456789c', '550e8400-e29b-41d4-a716-446655440006', 'Power Bank', 1, 49.99, '20000mAh portable power bank'),

                                                                                        -- Order 7 items
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef123456789d', '550e8400-e29b-41d4-a716-446655440007', 'Smart Watch', 1, 249.99, 'Fitness tracking smart watch'),
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef123456789e', '550e8400-e29b-41d4-a716-446655440007', 'Watch Band', 2, 29.99, 'Silicone sport watch band'),

                                                                                        -- Order 8 items
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef123456789f', '550e8400-e29b-41d4-a716-446655440008', 'Gaming Headset', 1, 159.99, 'Professional gaming headset with microphone'),
                                                                                        ('a1b2c3d4-e5f6-7890-abcd-ef1234567800', '550e8400-e29b-41d4-a716-446655440008', 'Mouse Pad', 1, 19.99, 'Large gaming mouse pad');