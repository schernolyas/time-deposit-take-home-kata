-- Logical model from the requirements -> physical names (PostgreSQL snake_case convention):
--   timeDeposits(id, planType, days, balance)            -> time_deposits(id, plan_type, days, balance)
--   withdrawals(id, timeDepositId, amount, date)         -> withdrawals(id, time_deposit_id, amount, date)
-- ASSUMPTION: Decimal = NUMERIC(19, 2) - money with cents, large enough for any realistic balance.

CREATE TABLE time_deposits
(
    id        INTEGER        NOT NULL PRIMARY KEY,
    plan_type VARCHAR(50)    NOT NULL,
    days      INTEGER        NOT NULL,
    balance   NUMERIC(19, 2) NOT NULL
);

CREATE TABLE withdrawals
(
    id              INTEGER        NOT NULL PRIMARY KEY,
    time_deposit_id INTEGER        NOT NULL REFERENCES time_deposits (id),
    amount          NUMERIC(19, 2) NOT NULL,
    date            DATE           NOT NULL
);

CREATE INDEX idx_withdrawals_time_deposit_id ON withdrawals (time_deposit_id);
