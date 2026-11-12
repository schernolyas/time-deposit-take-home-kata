package org.ikigaidigital.application;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.Withdrawal;

import java.util.List;

/** Read model returned by {@link org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase}. */
public record TimeDepositWithWithdrawals(TimeDeposit timeDeposit, List<Withdrawal> withdrawals) {

    public TimeDepositWithWithdrawals {
        withdrawals = List.copyOf(withdrawals);
    }
}
