-- Allow partial projection updates from Kafka events that may omit some material detail fields.
ALTER TABLE material_details
    ALTER COLUMN name DROP NOT NULL;

ALTER TABLE material_details
    ALTER COLUMN description DROP NOT NULL;

ALTER TABLE material_details
    ALTER COLUMN part1_title DROP NOT NULL;

ALTER TABLE material_details
    ALTER COLUMN part2_title DROP NOT NULL;

