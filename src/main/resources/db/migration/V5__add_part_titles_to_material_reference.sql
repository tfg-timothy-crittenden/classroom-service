-- Store the titles used by the material-service for richer classroom material display.
-- Nullable so existing rows remain valid until an upstream title update arrives.

ALTER TABLE material_reference
    ADD COLUMN IF NOT EXISTS part1_title VARCHAR(255);

ALTER TABLE material_reference
    ADD COLUMN IF NOT EXISTS part2_title VARCHAR(255);

