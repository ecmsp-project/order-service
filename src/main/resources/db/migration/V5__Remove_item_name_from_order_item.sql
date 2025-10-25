-- Remove item_name column from order_item table
-- This column was present in V1 and V2 but is not used in the application code
ALTER TABLE order_item DROP COLUMN IF EXISTS item_name;