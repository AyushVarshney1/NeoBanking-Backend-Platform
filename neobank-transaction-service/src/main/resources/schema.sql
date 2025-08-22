CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGSERIAL PRIMARY KEY,                           -- Auto increment ID
                                            transaction_id UUID NOT NULL UNIQUE,                -- Unique transaction reference
                                            account_number BIGINT NOT NULL,                     -- Initiator account
                                            target_account_number BIGINT,                       -- Receiver account (nullable for deposit/withdraw)
                                            amount DOUBLE PRECISION NOT NULL,                   -- Transaction amount
                                            transaction_type VARCHAR(20) NOT NULL,              -- Enum: DEPOSIT / WITHDRAW / TRANSFER
                                            status VARCHAR(20) NOT NULL,                        -- Enum: PENDING / SUCCESS / FAILED
                                            closing_balance DOUBLE PRECISION,                   -- Nullable, set when SUCCESS
                                            created_at TIMESTAMP NOT NULL DEFAULT NOW()         -- Auto timestamp on creation
);