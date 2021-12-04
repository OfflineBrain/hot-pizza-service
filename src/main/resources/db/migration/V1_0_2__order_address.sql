ALTER TABLE hotp.client_order
    ADD COLUMN IF NOT EXISTS address uuid NOT NULL default gen_random_uuid();