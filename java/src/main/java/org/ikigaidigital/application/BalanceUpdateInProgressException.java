package org.ikigaidigital.application;

/** Another balance update is running (in this or another application instance); nothing was changed. */
public class BalanceUpdateInProgressException extends RuntimeException {

    public BalanceUpdateInProgressException() {
        super("A balance update is already in progress; no balances were changed by this request.");
    }
}
