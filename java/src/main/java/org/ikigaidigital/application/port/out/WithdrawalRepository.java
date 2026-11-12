package org.ikigaidigital.application.port.out;

import org.ikigaidigital.domain.Withdrawal;

import java.util.List;

/** Outbound port to the withdrawal store. */
public interface WithdrawalRepository {

    /** All withdrawals of all time deposits, ordered by date and id. */
    List<Withdrawal> findAll();
}
