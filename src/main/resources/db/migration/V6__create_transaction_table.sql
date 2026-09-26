CREATE TABLE transactions
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    wallet_id   UUID         NOT NULL REFERENCES wallets (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    category_id UUID         NOT NULL REFERENCES categories (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    purpose     VARCHAR(120) NOT NULL,
    amount      DECIMAL(50, 2)        DEFAULT 0,
    type        VARCHAR(120) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    archived_at TIMESTAMPTZ
);