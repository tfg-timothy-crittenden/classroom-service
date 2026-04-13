-- Add optional assigned_to_role column to support role-specific material assignments
-- Safe to run on existing DBs: add the column only if it doesn't exist.

ALTER TABLE material_reference
    ADD COLUMN IF NOT EXISTS assigned_to_role VARCHAR(50);

