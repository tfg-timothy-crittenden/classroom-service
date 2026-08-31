ALTER TABLE member
    DROP CONSTRAINT IF EXISTS chk_member_name_not_blank,
    DROP CONSTRAINT IF EXISTS chk_member_surname_not_blank;

ALTER TABLE member
    ADD CONSTRAINT chk_member_name_not_blank
        CHECK (TRIM(name) <> ''),
    ADD CONSTRAINT chk_member_surname_not_blank
        CHECK (TRIM(surname) <> '');