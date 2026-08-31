-- Add optimistic-lock/version tracking column for replicated material details rows.
ALTER TABLE material_details
    ADD COLUMN IF NOT EXISTS version BIGINT;

UPDATE material_details
SET version = 0
WHERE version IS NULL;

ALTER TABLE material_details
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE material_details
    ALTER COLUMN version SET NOT NULL;

