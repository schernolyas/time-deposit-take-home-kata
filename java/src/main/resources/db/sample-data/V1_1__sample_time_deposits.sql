-- Demo data for local runs (Swagger UI). Not loaded by the integration tests.
-- One deposit per interesting case of the interest rules.
INSERT INTO time_deposits (id, plan_type, days, balance)
VALUES (1, 'basic', 30, 10000.00),    -- grace period: no interest
       (2, 'basic', 31, 10000.00),    -- 1% p.a.
       (3, 'student', 365, 5000.00),  -- last day with interest
       (4, 'student', 366, 5000.00),  -- after one year: no interest
       (5, 'premium', 45, 20000.00),  -- not yet: no interest
       (6, 'premium', 46, 20000.00),  -- 5% p.a.
       (7, 'gold', 100, 1000.00);     -- unknown plan: no interest

INSERT INTO withdrawals (id, time_deposit_id, amount, date)
VALUES (1, 2, 250.00, '2026-09-15'),
       (2, 2, 100.00, '2026-10-01'),
       (3, 6, 1000.00, '2026-10-20');
