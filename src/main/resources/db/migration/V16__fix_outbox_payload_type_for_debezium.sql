-- Ensure integration_outbox.payload is TEXT JSON, not OID large object pointer.
DO $$
DECLARE
    payload_type text;
BEGIN
    SELECT data_type
      INTO payload_type
      FROM information_schema.columns
     WHERE table_schema = 'public'
       AND table_name = 'integration_outbox'
       AND column_name = 'payload';

    IF payload_type = 'oid' THEN
        ALTER TABLE public.integration_outbox
            ALTER COLUMN payload TYPE TEXT
            USING convert_from(lo_get(payload), 'UTF8');
    END IF;
END $$;

