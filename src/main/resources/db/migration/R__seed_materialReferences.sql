INSERT INTO material_reference (
    id, material_id, classroom_id, name, description, assigned_to_role
) VALUES (
             1, 1, 1, 'TOEFL Practice Test 1', 'TOEFL Practice Test 1', 'TEACHER'
         )
ON CONFLICT (id) DO NOTHING;

INSERT INTO material_reference (
    id, material_id, classroom_id, name, description, assigned_to_role
) VALUES (
             2, 26, 1, 'TOEFL Practice Test 2', 'TOEFL Practice Test 2', 'STUDENT'
         )
ON CONFLICT (id) DO NOTHING;