CREATE TABLE transactions (
  id               BIGSERIAL PRIMARY KEY,
  account_number   VARCHAR(20) NOT NULL,
  trx_amount       DECIMAL(19,2) NOT NULL,
  description      VARCHAR(255) NOT NULL,
  trx_date         DATE NOT NULL,
  trx_time         TIME NOT NULL,
  customer_id      BIGINT NOT NULL,
  version          BIGINT NOT NULL DEFAULT 0,
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tx_customer ON transactions(customer_id);
CREATE INDEX idx_tx_account  ON transactions(account_number);
CREATE INDEX idx_tx_desc     ON transactions(description);
CREATE INDEX idx_tx_date     ON transactions(trx_date);

-- can add unique index if required