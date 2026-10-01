CREATE TABLE notification (
                              id BIGSERIAL PRIMARY KEY,
                              task_id BIGINT NOT NULL,
                              message VARCHAR(255) NOT NULL,
                              read BOOLEAN NOT NULL,
                              created_at TIMESTAMP NOT NULL
);