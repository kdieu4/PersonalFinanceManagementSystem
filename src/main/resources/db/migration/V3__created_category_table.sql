CREATE TABLE categories
(
    id          uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    user_id     uuid REFERENCES users (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    parent_id   uuid REFERENCES categories (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    name        VARCHAR(120),
    type        VARCHAR(120),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    archived_at TIMESTAMPTZ
)