CREATE TABLE wallets
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL REFERENCES users (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    name        VARCHAR(120) NOT NULL,
    balance     DECIMAL(50, 2)        DEFAULT 0,
    currency    VARCHAR(10)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    archived_at TIMESTAMPTZ
);