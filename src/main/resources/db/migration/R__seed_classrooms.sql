-- Seed classrooms only if they do not already exist

INSERT INTO classroom (id, name, description, created_at, updated_at, join_code)
SELECT 1, 'TOEFLMJ1930', null, NOW(), NOW(), 'A1B2C3'
WHERE NOT EXISTS (SELECT 1 FROM classroom WHERE id=1);


INSERT INTO classroom (id, name, description, created_at, updated_at, join_code)
SELECT 2, 'TOEFLX2000', null, NOW(), NOW(), 'X9Y8Z7'
WHERE NOT EXISTS (SELECT 1 FROM classroom WHERE id=2);


INSERT INTO classroom (id, name, description, created_at, updated_at, join_code)
SELECT 3, 'TOEFLM1930', null, NOW(), NOW(), 'M3N4P5'
WHERE NOT EXISTS (SELECT 1 FROM classroom WHERE id = 3);

