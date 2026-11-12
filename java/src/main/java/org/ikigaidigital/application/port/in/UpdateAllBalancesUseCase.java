package org.ikigaidigital.application.port.in;

/** Applies one month of interest to every time deposit and persists the new balances. */
public interface UpdateAllBalancesUseCase {

    void updateAllBalances();
}
