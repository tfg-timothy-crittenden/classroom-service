ALTER TABLE member
    ALTER COLUMN name SET NOT NULL,
    ALTER COLUMN surname SET NOT NULL;

ALTER TABLE member
    ADD CONSTRAINT chk_member_name_not_blank CHECK (TRIM(name) <> ''),
    ADD CONSTRAINT chk_member_surname_not_blank CHECK (TRIM(surname) <> '');

