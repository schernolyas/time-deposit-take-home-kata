package org.ikigaidigital.application.service;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.Withdrawal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GetAllTimeDepositsServiceTest {

    @Test
    void getAllTimeDeposits_attachesEachDepositsWithdrawalsInOrder() {
        InMemoryTimeDepositRepository deposits = new InMemoryTimeDepositRepository(
                new TimeDeposit(1, "basic", 100.00, 10),
                new TimeDeposit(2, "premium", 200.00, 60));
        Withdrawal first = new Withdrawal(10, 2, new BigDecimal("5.00"), LocalDate.of(2026, 9, 1));
        Withdrawal second = new Withdrawal(11, 2, new BigDecimal("7.50"), LocalDate.of(2026, 10, 1));
        GetAllTimeDepositsService service = new GetAllTimeDepositsService(deposits, () -> List.of(first, second));

        List<TimeDepositWithWithdrawals> result = service.getAllTimeDeposits();

        assertThat(result).extracting(r -> r.timeDeposit().getId()).containsExactly(1, 2);
        assertThat(result.get(0).withdrawals()).isEmpty();
        assertThat(result.get(1).withdrawals()).containsExactly(first, second);
    }
}
