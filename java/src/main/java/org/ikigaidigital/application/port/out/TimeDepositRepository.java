package org.ikigaidigital.application.port.out;

import org.ikigaidigital.TimeDeposit;

import java.util.List;

/** Outbound port to the time deposit store. */
public interface TimeDepositRepository {

    /** All time deposits, ordered by id. */
    List<TimeDeposit> findAll();

    /** Persists the current {@link TimeDeposit#getBalance() balance} of each given deposit. */
    void updateBalances(List<TimeDeposit> timeDeposits);
}
