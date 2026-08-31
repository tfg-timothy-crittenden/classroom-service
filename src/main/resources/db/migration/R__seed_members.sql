-- Classroom 1: Mary Doe (teacher), John Smith (student)

INSERT INTO membership (
    classroom_id,
    user_id,
    role,
    created_at,
    updated_at
)
SELECT
    1,
    2,
    'TEACHER',
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM membership
    WHERE classroom_id = 1
      AND user_id = 2
);

INSERT INTO membership (
    classroom_id,
    user_id,
    role,
    created_at,
    updated_at
)
SELECT
    1,
    1,
    'STUDENT',
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM membership
    WHERE classroom_id = 1
      AND user_id = 1
);

-- Classroom 2: Robert Johnson (teacher), Mary Doe (student)

INSERT INTO membership (
    classroom_id,
    user_id,
    role,
    created_at,
    updated_at
)
SELECT
    2,
    3,
    'TEACHER',
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM membership
    WHERE classroom_id = 2
      AND user_id = 3
);

INSERT INTO membership (
    classroom_id,
    user_id,
    role,
    created_at,
    updated_at
)
SELECT
    2,
    2,
    'STUDENT',
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM membership
    WHERE classroom_id = 2
      AND user_id = 2
);

-- Classroom 3: John Smith (student), Robert Johnson (teacher)

INSERT INTO membership (
    classroom_id,
    user_id,
    role,
    created_at,
    updated_at
)
SELECT
    3,
    1,
    'STUDENT',
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM membership
    WHERE classroom_id = 3
      AND user_id = 1
);

INSERT INTO membership (
    classroom_id,
    user_id,
    role,
    created_at,
    updated_at
)
SELECT
    3,
    3,
    'TEACHER',
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM membership
    WHERE classroom_id = 3
      AND user_id = 3
);