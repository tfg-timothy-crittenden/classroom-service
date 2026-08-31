-- Remove legacy material display columns from material_reference.
ALTER TABLE material_reference
    DROP COLUMN IF EXISTS name,
    DROP COLUMN IF EXISTS description,
    DROP COLUMN IF EXISTS part1_title,
    DROP COLUMN IF EXISTS part2_title;

