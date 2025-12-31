-- V2__Insert_example_data.sql
-- Flyway migration to insert example data into orders and order_item tables

-- Insert example orders
INSERT INTO orders (order_id, reservation_id, order_status, date, client_id) VALUES
                                                                 ('550e8400-e29b-41d4-a716-446655440001', 'c1d2e3f4-a5b6-7890-cdef-012345678901', 'PENDING', '2024-01-15 10:30:00', '123e4567-e89b-12d3-a456-426614174001'),
                                                                 ('550e8400-e29b-41d4-a716-446655440002', 'c1d2e3f4-a5b6-7890-cdef-012345678902', 'PAID', '2024-01-16 14:22:15', '123e4567-e89b-12d3-a456-426614174002'),
                                                                 ('550e8400-e29b-41d4-a716-446655440003', 'c1d2e3f4-a5b6-7890-cdef-012345678903', 'PROCESSING', '2024-01-17 09:45:30', '123e4567-e89b-12d3-a456-426614174001'),
                                                                 ('550e8400-e29b-41d4-a716-446655440004', 'c1d2e3f4-a5b6-7890-cdef-012345678904', 'CANCELLED', '2024-01-18 16:10:45', '123e4567-e89b-12d3-a456-426614174003'),
                                                                 ('550e8400-e29b-41d4-a716-446655440005', 'c1d2e3f4-a5b6-7890-cdef-012345678905', 'PROCESSING', '2024-01-19 11:20:00', '123e4567-e89b-12d3-a456-426614174002'),
                                                                 ('550e8400-e29b-41d4-a716-446655440006', 'c1d2e3f4-a5b6-7890-cdef-012345678906', 'PAID', '2024-01-20 13:55:12', '123e4567-e89b-12d3-a456-426614174004'),
                                                                 ('550e8400-e29b-41d4-a716-446655440007', 'c1d2e3f4-a5b6-7890-cdef-012345678907', 'PENDING', '2024-01-21 08:15:30', '123e4567-e89b-12d3-a456-426614174001'),
                                                                 ('550e8400-e29b-41d4-a716-446655440008', 'c1d2e3f4-a5b6-7890-cdef-012345678908', 'FAILED', '2024-01-22 15:40:25', '123e4567-e89b-12d3-a456-426614174005'),
                                                                --second order for the first client for testing
                                                                 ('550e8400-e29b-41d4-a716-446655440009', 'c1d2e3f4-a5b6-7890-cdef-012345678909', 'PENDING', '2024-01-15 11:30:00', '123e4567-e89b-12d3-a456-426614174001');

-- Insert example order items
INSERT INTO order_item (order_item_id, item_id, variant_id, order_id, item_name, quantity, price, image_url, description, is_returnable) VALUES
                                                                                        -- Order 1 items
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef1234567890', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'b1b2c3d4-e5f6-7890-abcd-ef1234567890', '550e8400-e29b-41d4-a716-446655440001', 'Wireless Headphones', 2, 129.99, 'https://example.com/images/headphones.jpg', 'Premium noise-cancelling wireless headphones', true),
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef1234567891', 'a1b2c3d4-e5f6-7890-abcd-ef1234567891', 'b1b2c3d4-e5f6-7890-abcd-ef1234567891', '550e8400-e29b-41d4-a716-446655440001', 'Phone Case', 1, 24.99, 'https://example.com/images/phone-case.jpg', 'Protective silicone phone case', true),

                                                                                        -- Order 2 items
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef1234567892', 'a1b2c3d4-e5f6-7890-abcd-ef1234567892', 'b1b2c3d4-e5f6-7890-abcd-ef1234567892', '550e8400-e29b-41d4-a716-446655440002', 'Laptop Stand', 1, 89.99, 'https://example.com/images/laptop-stand.jpg', 'Adjustable aluminum laptop stand', true),
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef1234567893', 'a1b2c3d4-e5f6-7890-abcd-ef1234567893', 'b1b2c3d4-e5f6-7890-abcd-ef1234567893', '550e8400-e29b-41d4-a716-446655440002', 'USB-C Cable', 3, 19.99, 'https://example.com/images/usb-cable.jpg', '6ft USB-C charging cable', false),
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef1234567894', 'a1b2c3d4-e5f6-7890-abcd-ef1234567894', 'b1b2c3d4-e5f6-7890-abcd-ef1234567894', '550e8400-e29b-41d4-a716-446655440002', 'Wireless Mouse', 1, 45.99, 'https://example.com/images/mouse.jpg', 'Ergonomic wireless optical mouse', true),

                                                                                        -- Order 3 items
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef1234567895', 'a1b2c3d4-e5f6-7890-abcd-ef1234567895', 'b1b2c3d4-e5f6-7890-abcd-ef1234567895', '550e8400-e29b-41d4-a716-446655440003', 'Monitor', 1, 299.99, 'https://example.com/images/monitor.jpg', '27-inch 4K LED monitor', true),
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef1234567896', 'a1b2c3d4-e5f6-7890-abcd-ef1234567896', 'b1b2c3d4-e5f6-7890-abcd-ef1234567896', '550e8400-e29b-41d4-a716-446655440003', 'Keyboard', 1, 79.99, 'https://example.com/images/keyboard.jpg', 'Mechanical gaming keyboard', true),

                                                                                        -- Order 4 items (cancelled order)
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef1234567897', 'a1b2c3d4-e5f6-7890-abcd-ef1234567897', 'b1b2c3d4-e5f6-7890-abcd-ef1234567897', '550e8400-e29b-41d4-a716-446655440004', 'Webcam', 1, 149.99, 'https://example.com/images/webcam.jpg', 'HD webcam with auto-focus', true),

                                                                                        -- Order 5 items
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef1234567898', 'a1b2c3d4-e5f6-7890-abcd-ef1234567898', 'b1b2c3d4-e5f6-7890-abcd-ef1234567898', '550e8400-e29b-41d4-a716-446655440005', 'Tablet', 1, 399.99, 'https://example.com/images/tablet.jpg', '10-inch Android tablet', true),
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef1234567899', 'a1b2c3d4-e5f6-7890-abcd-ef1234567899', 'b1b2c3d4-e5f6-7890-abcd-ef1234567899', '550e8400-e29b-41d4-a716-446655440005', 'Tablet Case', 1, 34.99, 'https://example.com/images/tablet-case.jpg', 'Leather tablet case with stand', true),
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef123456789a', 'a1b2c3d4-e5f6-7890-abcd-ef123456789a', 'b1b2c3d4-e5f6-7890-abcd-ef123456789a', '550e8400-e29b-41d4-a716-446655440005', 'Screen Protector', 2, 12.99, 'https://example.com/images/screen-protector.jpg', 'Tempered glass screen protector', false),

                                                                                        -- Order 6 items
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef123456789b', 'a1b2c3d4-e5f6-7890-abcd-ef123456789b', 'b1b2c3d4-e5f6-7890-abcd-ef123456789b', '550e8400-e29b-41d4-a716-446655440006', 'Bluetooth Speaker', 1, 79.99, 'https://example.com/images/speaker.jpg', 'Portable waterproof Bluetooth speaker', true),
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef123456789c', 'a1b2c3d4-e5f6-7890-abcd-ef123456789c', 'b1b2c3d4-e5f6-7890-abcd-ef123456789c', '550e8400-e29b-41d4-a716-446655440006', 'Power Bank', 1, 49.99, 'https://example.com/images/power-bank.jpg', '20000mAh portable power bank', true),

                                                                                        -- Order 7 items
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef123456789d', 'a1b2c3d4-e5f6-7890-abcd-ef123456789d', 'b1b2c3d4-e5f6-7890-abcd-ef123456789d', '550e8400-e29b-41d4-a716-446655440007', 'Smart Watch', 1, 249.99, 'https://example.com/images/smartwatch.jpg', 'Fitness tracking smart watch', true),
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef123456789e', 'a1b2c3d4-e5f6-7890-abcd-ef123456789e', 'b1b2c3d4-e5f6-7890-abcd-ef123456789e', '550e8400-e29b-41d4-a716-446655440007', 'Watch Band', 2, 29.99, 'https://example.com/images/watch-band.jpg', 'Silicone sport watch band', false),

                                                                                        -- Order 8 items
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef123456789f', 'a1b2c3d4-e5f6-7890-abcd-ef123456789f', 'b1b2c3d4-e5f6-7890-abcd-ef123456789f', '550e8400-e29b-41d4-a716-446655440008', 'Gaming Headset', 1, 159.99, 'https://example.com/images/gaming-headset.jpg', 'Professional gaming headset with microphone', true),
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef1234567800', 'a1b2c3d4-e5f6-7890-abcd-ef1234567800', 'b1b2c3d4-e5f6-7890-abcd-ef1234567800', '550e8400-e29b-41d4-a716-446655440008', 'Mouse Pad', 1, 19.99, 'https://example.com/images/mouse-pad.jpg', 'Large gaming mouse pad', true),

                                                                                        -- Order 9 items (second order for the first client) - testing
                                                                                        ('f1b2c3d4-e5f6-7890-abcd-ef1234567801', 'a1b2c3d4-e5f6-7890-abcd-ef1234567801', 'b1b2c3d4-e5f6-7890-abcd-ef1234567801', '550e8400-e29b-41d4-a716-446655440009', 'Mouse Pad', 1, 19.99, 'https://example.com/images/mouse-pad.jpg', 'Large gaming mouse pad', true);
