-- Seed members table with relevant data for 3 classrooms and 4 users
-- Assumes classrooms with ids 1, 2, 3 and users with ids 1, 2, 3, 4 already exist

-- Classroom 1: Add John Smith (teacher), Mary Doe (student)
INSERT INTO member (classroom_id, user_id, name, surname, role, created_at, updated_at)
SELECT 1, 1, 'John', 'Smith', 'TEACHER', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM member WHERE classroom_id = 1 AND user_id = 1);
INSERT INTO member (classroom_id, user_id, name, surname, role, created_at, updated_at)
SELECT 1, 2, 'Mary', 'Doe', 'STUDENT', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM member WHERE classroom_id = 1 AND user_id = 2);

-- Classroom 2: Add Robert Johnson (teacher), John Smith (student)
INSERT INTO member (classroom_id, user_id, name, surname, role, created_at, updated_at)
SELECT 2, 3, 'Robert', 'Johnson', 'TEACHER', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM member WHERE classroom_id = 2 AND user_id = 3);
INSERT INTO member (classroom_id, user_id, name, surname, role, created_at, updated_at)
SELECT 2, 1, 'John', 'Smith', 'STUDENT', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM member WHERE classroom_id = 2 AND user_id = 1);

-- Classroom 3: Add Admin (teacher), Mary Doe (student)
INSERT INTO member (classroom_id, user_id, name, surname, role, created_at, updated_at)
SELECT 3, 4, 'admin', 'admin', 'TEACHER', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM member WHERE classroom_id = 3 AND user_id = 4);
INSERT INTO member (classroom_id, user_id, name, surname, role, created_at, updated_at)
SELECT 3, 2, 'Mary', 'Doe', 'STUDENT', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM member WHERE classroom_id = 3 AND user_id = 2);
