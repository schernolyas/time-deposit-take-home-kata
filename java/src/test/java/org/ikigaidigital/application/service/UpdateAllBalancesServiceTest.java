package org.ikigaidigital.application.service;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateAllBalancesServiceTest {

    @Test
    void updateAllBalances_appliesOneMonthOfInterestAndPersistsIt() {
        InMemoryTimeDepositRepository repository = new InMemoryTimeDepositRepository(
                new TimeDeposit(1, "basic", 1_234_567.00, 31),
                new TimeDeposit(2, "premium", 1_234_567.00, 45),
                new TimeDeposit(3, "student", 1_234_567.00, 31));
        UpdateAllBalancesService service = new UpdateAllBalancesService(repository, new TimeDepositCalculator());

        service.updateAllBalances();

        assertThat(repository.balanceOf(1)).isEqualTo(1_235_595.81);
        assertThat(repository.balanceOf(2)).isEqualTo(1_234_567.00);
        assertThat(repository.balanceOf(3)).isEqualTo(1_237_653.42);
    }

    @Test
    void updateAllBalances_doesNotChangeDays() {
        InMemoryTimeDepositRepository repository =
                new InMemoryTimeDepositRepository(new TimeDeposit(1, "basic", 100.00, 31));

        new UpdateAllBalancesService(repository, new TimeDepositCalculator()).updateAllBalances();

        assertThat(repository.findAll().get(0).getDays()).isEqualTo(31);
    }
}
