INSERT INTO member_profile (
    user_id,
    version,
    first_name,
    last_name
)
VALUES
    (1, 0, 'John', 'Smith'),
    (2, 0, 'Mary', 'Doe'),
    (3, 0, 'Robert', 'Johnson')
ON CONFLICT (user_id) DO NOTHING;