package org.ikigaidigital.application.service;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.application.port.in.UpdateAllBalancesUseCase;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Loads all deposits, lets the (unchanged) {@link TimeDepositCalculator} apply one month of interest and
 * stores the new balances in one transaction.
 * <p>
 * ASSUMPTION: the update changes balances only. {@code days} is not incremented, because the original
 * {@code updateBalance} never touched it and nothing in the requirements says the endpoint should
 * (deposit age is presumably maintained by another process).
 * <p>
 * Concurrent calls are prevented by {@link ExclusiveUpdateAllBalances}, which wraps this service.
 * <p>
 * ASSUMPTION: all deposits are processed in one transaction. Fine for the kata's data volume; a real
 * bank-sized portfolio would be processed in chunks.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAllBalancesService implements UpdateAllBalancesUseCase {

    private final TimeDepositRepository timeDepositRepository;
    private final TimeDepositCalculator calculator;

    @Override
    public void updateAllBalances() {
        List<TimeDeposit> timeDeposits = timeDepositRepository.findAll();
        calculator.updateBalance(timeDeposits);
        timeDepositRepository.updateBalances(timeDeposits);
    }
}
