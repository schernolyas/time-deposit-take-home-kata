package org.ikigaidigital.domain;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Conversion of the legacy {@code double} balances to money with cents, shared by the API and persistence. */
@UtilityClass
public class Money {

    public static final int SCALE = 2;

    /**
     * ASSUMPTION: money is stored and exposed with cents (NUMERIC(19,2) / 2 decimals). The legacy calculator adds in
     * double, which can leave binary noise (think 0.1 + 0.2 = 0.30000000000000004); this removes exactly that noise.
     * Interest itself is already rounded to cents by {@link org.ikigaidigital.TimeDepositCalculator}.
     */
    public static BigDecimal toCents(Double amount) {
        return amount == null ? null : BigDecimal.valueOf(amount).setScale(SCALE, RoundingMode.HALF_UP);
    }
}
