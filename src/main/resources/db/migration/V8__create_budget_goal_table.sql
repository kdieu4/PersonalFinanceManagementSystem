CREATE TABLE budgets
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID           NOT NULL REFERENCES users (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    category_id UUID           NOT NULL REFERENCES categories (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    name        VARCHAR(255)   NOT NULL,
    amount      DECIMAL(19, 2) NOT NULL CHECK (amount > 0),
    start_date  DATE           NOT NULL,
    end_date    DATE           NOT NULL CHECK (end_date >= start_date),
    created_at  TIMESTAMPTZ    NOT NULL,
    updated_at  TIMESTAMPTZ    NOT NULL,
    archived_at TIMESTAMPTZ
);

CREATE TABLE goals
(
    id             UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    user_id        UUID           NOT NULL REFERENCES users (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    name           VARCHAR(255)   NOT NULL,
    target_amount  DECIMAL(19, 2) NOT NULL CHECK (target_amount > 0),
    current_amount DECIMAL(19, 2) NOT NULL DEFAULT 0 CHECK (current_amount >= 0),
    target_date    DATE           NOT NULL,
    status         VARCHAR(30)    NOT NULL,
    created_at     TIMESTAMPTZ    NOT NULL,
    updated_at     TIMESTAMPTZ    NOT NULL,
    archived_at    TIMESTAMPTZ
);
