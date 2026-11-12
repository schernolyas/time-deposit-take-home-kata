package org.ikigaidigital.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Money withdrawn from a time deposit.
 * <p>
 * Modelled next to - not inside - the shared {@link org.ikigaidigital.TimeDeposit} class, which must not
 * get breaking changes. The link is the {@code timeDepositId}, as in the database.
 * <p>
 * ASSUMPTION: withdrawals are historical records only. The kata gives no rule saying they affect interest
 * or the balance update, and the existing balance logic must stay unchanged, so they are not used there.
 */
public record Withdrawal(int id, int timeDepositId, BigDecimal amount, LocalDate date) {

    public Withdrawal {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(date, "date");
    }
}
