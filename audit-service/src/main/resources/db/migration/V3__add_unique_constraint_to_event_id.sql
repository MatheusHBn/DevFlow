ALTER TABLE audit_log
ADD CONSTRAINT uq_audit_log_event_id UNIQUE (event_id);