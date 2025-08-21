-- Accounts table
CREATE TABLE IF NOT EXISTS accounts (
                                        id BIGSERIAL PRIMARY KEY,           -- auto-generated ID
                                        account_number BIGINT NOT NULL UNIQUE,
                                        user_id UUID NOT NULL,
                                        balance DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                        status VARCHAR(50) NOT NULL,
                                        created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- KYC table
CREATE TABLE IF NOT EXISTS kyc (
                                   id BIGSERIAL PRIMARY KEY,           -- auto-generated ID
                                   pan_number VARCHAR(10) NOT NULL UNIQUE,
                                   aadhaar_number VARCHAR(12) NOT NULL UNIQUE,
                                   address VARCHAR(255) NOT NULL,
                                   account_number BIGINT NOT NULL UNIQUE,
                                   CONSTRAINT fk_account
                                       FOREIGN KEY (account_number)
                                           REFERENCES accounts(account_number)
                                           ON DELETE CASCADE
);
