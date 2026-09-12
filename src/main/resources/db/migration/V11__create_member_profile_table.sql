CREATE TABLE IF NOT EXISTS member_profile  (
    user_id BIGINT PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

INSERT INTO member_profile (
    user_id,
    version,
    first_name,
    last_name
)
SELECT DISTINCT ON (user_id)
    user_id,
    0,
    name,
    surname
FROM membership
ORDER BY user_id, updated_at DESC;