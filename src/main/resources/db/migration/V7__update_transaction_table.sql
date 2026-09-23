ALTER TABLE transactions
    ADD COLUMN description VARCHAR(255),
    ADD COLUMN transaction_date DATE,
    ADD COLUMN user_id uuid REFERENCES users (id) ON UPDATE CASCADE ON DELETE NO ACTION;