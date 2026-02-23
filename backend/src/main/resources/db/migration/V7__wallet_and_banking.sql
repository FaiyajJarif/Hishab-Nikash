-- -- ===========================
-- -- WALLET
-- -- ===========================

-- CREATE TABLE wallets (
--     wallet_id SERIAL PRIMARY KEY,
--     user_id INTEGER NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
--     balance NUMERIC(18,2) NOT NULL DEFAULT 0,
--     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
-- );

-- -- ===========================
-- -- WALLET LEDGER (audit log)
-- -- ===========================

-- CREATE TABLE wallet_ledger_entries (
--     entry_id SERIAL PRIMARY KEY,
--     wallet_id INTEGER NOT NULL REFERENCES wallets(wallet_id) ON DELETE CASCADE,
--     type VARCHAR(40) NOT NULL,
--     amount NUMERIC(18,2) NOT NULL,
--     description VARCHAR(255),
--     reference_id INTEGER,
--     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
-- );

-- CREATE INDEX idx_wallet_ledger_wallet ON wallet_ledger_entries(wallet_id);SELECT * FROM wallets;
