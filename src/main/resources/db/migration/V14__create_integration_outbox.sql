CREATE TABLE IF NOT EXISTS integration_outbox (
    id BIGSERIAL PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id BIGINT NOT NULL,
    event_type VARCHAR(150) NOT NULL,
    event_key VARCHAR(150),
    payload TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_integration_outbox_created_at_id
    ON integration_outbox (created_at, id);

