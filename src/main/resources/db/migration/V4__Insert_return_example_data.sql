-- V4__Insert_return_example_data.sql
-- Flyway migration to insert example data into returns and return_item tables

-- Insert example returns
INSERT INTO returns (return_id, order_id, status, created_at) VALUES
    -- Return for order 2 (PAID order with multiple items)
    ('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 'REQUESTED', '2024-01-25 10:30:00'),

    -- Return for order 6 (PAID order with 2 items)
    ('660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440006', 'PROCESSING', '2024-01-26 14:15:00'),

    -- Return for order 3 (PROCESSING order with monitor and keyboard)
    ('660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 'COMPLETED', '2024-01-27 09:45:00');

-- Insert example return items
INSERT INTO return_item (return_item_id, item_id, variant_id, return_id, quantity, reason) VALUES
    -- Return 1 items (from order 2)
    ('770e8400-e29b-41d4-a716-446655440001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567892', 'b1b2c3d4-e5f6-7890-abcd-ef1234567892', '660e8400-e29b-41d4-a716-446655440001', 1, 'Product not as described'),
    ('770e8400-e29b-41d4-a716-446655440002', 'a1b2c3d4-e5f6-7890-abcd-ef1234567894', 'b1b2c3d4-e5f6-7890-abcd-ef1234567894', '660e8400-e29b-41d4-a716-446655440001', 1, 'Defective item'),

    -- Return 2 items (from order 6)
    ('770e8400-e29b-41d4-a716-446655440003', 'a1b2c3d4-e5f6-7890-abcd-ef123456789b', 'b1b2c3d4-e5f6-7890-abcd-ef123456789b', '660e8400-e29b-41d4-a716-446655440002', 1, 'Changed my mind'),

    -- Return 3 items (from order 3)
    ('770e8400-e29b-41d4-a716-446655440004', 'a1b2c3d4-e5f6-7890-abcd-ef1234567895', 'b1b2c3d4-e5f6-7890-abcd-ef1234567895', '660e8400-e29b-41d4-a716-446655440003', 1, 'Found better price elsewhere'),
    ('770e8400-e29b-41d4-a716-446655440005', 'a1b2c3d4-e5f6-7890-abcd-ef1234567896', 'b1b2c3d4-e5f6-7890-abcd-ef1234567896', '660e8400-e29b-41d4-a716-446655440003', 1, 'Wrong keyboard type ordered');
