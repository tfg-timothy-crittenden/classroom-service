-- noinspection SqlResolve
INSERT INTO material_reference (
    id, material_id, classroom_id, name, description, part1_title, part2_title, assigned_to_role
)
SELECT 1, 1, 1, 'TOEFL Practice Test 1', 'TOEFL Practice Test 1', 'Part 1 - TOEFL Practice Test 1', 'Part 2 - TOEFL Practice Test 1', 'TEACHER'
WHERE EXISTS (SELECT 1 FROM classroom WHERE id = 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO material_reference (
    id, material_id, classroom_id, name, description, part1_title, part2_title, assigned_to_role
)
SELECT 2, 26, 1, 'TOEFL Practice Test 2', 'TOEFL Practice Test 2', 'Part 1 - TOEFL Practice Test 2', 'Part 2 - TOEFL Practice Test 2', 'STUDENT'
WHERE EXISTS (SELECT 1 FROM classroom WHERE id = 1)
ON CONFLICT (id) DO NOTHING;