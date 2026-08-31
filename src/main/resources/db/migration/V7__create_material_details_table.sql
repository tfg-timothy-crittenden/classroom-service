-- Create the material details table for enriched material metadata.
CREATE TABLE IF NOT EXISTS material_details (
    material_id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    part1_title VARCHAR(255) NOT NULL,
    part2_title VARCHAR(255) NOT NULL
);

