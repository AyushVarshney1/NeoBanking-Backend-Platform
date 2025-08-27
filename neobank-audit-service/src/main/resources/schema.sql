CREATE TABLE IF NOT EXISTS audit_logs (
                            id BIGSERIAL PRIMARY KEY,

                            transaction_id VARCHAR(255) NOT NULL,
                            account_number BIGINT NOT NULL,
                            user_id VARCHAR(255) NOT NULL,
                            beneficiary_account_number BIGINT,

                            type VARCHAR(50) NOT NULL,
                            amount NUMERIC(18, 2) NOT NULL,
                            status VARCHAR(50) NOT NULL,

                            created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);