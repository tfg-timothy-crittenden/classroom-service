-- Core classroom tables
CREATE TABLE IF NOT EXISTS classroom (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS member (
    id BIGSERIAL PRIMARY KEY,
    classroom_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    name VARCHAR(255),
    surname VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_member_classroom
        FOREIGN KEY (classroom_id) REFERENCES classroom(id) ON DELETE CASCADE,
    CONSTRAINT uk_member_classroom_user
        UNIQUE (classroom_id, user_id)
);

CREATE TABLE IF NOT EXISTS material_reference (
    id BIGSERIAL PRIMARY KEY,
    classroom_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    assigned_to_role VARCHAR(50),
    CONSTRAINT fk_material_reference_classroom
        FOREIGN KEY (classroom_id) REFERENCES classroom(id) ON DELETE CASCADE,
    CONSTRAINT uk_material_reference_classroom_material
        UNIQUE (classroom_id, material_id)
);
