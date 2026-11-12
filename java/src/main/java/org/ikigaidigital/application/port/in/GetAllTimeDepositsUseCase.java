package org.ikigaidigital.application.port.in;

import org.ikigaidigital.application.TimeDepositWithWithdrawals;

import java.util.List;

/** Lists all time deposits together with their withdrawals. */
public interface GetAllTimeDepositsUseCase {

    List<TimeDepositWithWithdrawals> getAllTimeDeposits();
}
