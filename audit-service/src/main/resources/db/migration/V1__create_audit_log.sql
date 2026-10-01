CREATE TABLE audit_log (
                           id BIGSERIAL PRIMARY KEY,
                           event_type VARCHAR(50) NOT NULL,
                           entity_type VARCHAR(50) NOT NULL,
                           entity_id BIGINT NOT NULL,
                           description TEXT NOT NULL,
                           created_at TIMESTAMP NOT NULL
);