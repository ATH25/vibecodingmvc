-- Add optional description column to beer table
-- H2-compatible DDL

ALTER TABLE beer
    ADD COLUMN IF NOT EXISTS description VARCHAR(1000);
