-- Seed members table with relevant data for 3 classrooms and 4 users
-- Assumes classrooms with ids 1, 2, 3 and users with ids 1, 2, 3, 4 already exist


-- Classroom 1: Mary Doe (teacher), John Smith (student)
INSERT INTO member (classroom_id, user_id, name, surname, role, created_at, updated_at)
SELECT 1, 2, 'Mary', 'Doe', 'TEACHER', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM member WHERE classroom_id = 1 AND user_id = 2);
INSERT INTO member (classroom_id, user_id, name, surname, role, created_at, updated_at)
SELECT 1, 1, 'John', 'Smith', 'STUDENT', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM member WHERE classroom_id = 1 AND user_id = 1);


-- Classroom 2: Robert Johnson (TEACHER), Mary Doe (student)
INSERT INTO member (classroom_id, user_id, name, surname, role, created_at, updated_at)
SELECT 2, 3, 'Robert', 'Johnson', 'TEACHER', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM member WHERE classroom_id = 2 AND user_id = 3);
INSERT INTO member (classroom_id, user_id, name, surname, role, created_at, updated_at)
SELECT 2, 2, 'Mary', 'Doe', 'STUDENT', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM member WHERE classroom_id = 2 AND user_id = 2);


-- Classroom 3: John Smith (student), Robert Johnson (TEACHER)
INSERT INTO member (classroom_id, user_id, name, surname, role, created_at, updated_at)
SELECT 3, 1, 'John', 'Smith', 'STUDENT', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM member WHERE classroom_id = 3 AND user_id = 1);
INSERT INTO member (classroom_id, user_id, name, surname, role, created_at, updated_at)
SELECT 3, 3, 'Robert', 'Johnson', 'TEACHER', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM member WHERE classroom_id = 3 AND user_id = 3);
