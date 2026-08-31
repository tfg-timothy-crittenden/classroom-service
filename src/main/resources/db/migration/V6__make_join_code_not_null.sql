-- Safe to repeat; will fail if NULL values exist.
ALTER TABLE classroom
    ALTER COLUMN join_code SET NOT NULL;

-- Make recreation idempotent.
ALTER TABLE classroom
    DROP CONSTRAINT IF EXISTS uq_classroom_join_code;

ALTER TABLE classroom
    ADD CONSTRAINT uq_classroom_join_code UNIQUE (join_code);