-- Make join_code NOT NULL (idempotent, will fail if NULLs exist)
ALTER TABLE classroom ALTER COLUMN join_code SET NOT NULL;

-- Add unique constraint to join_code (idempotent, will fail if duplicates exist)
ALTER TABLE classroom ADD CONSTRAINT uq_classroom_join_code UNIQUE (join_code);
