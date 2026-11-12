package org.ikigaidigital.application.service;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.application.BalanceUpdateInProgressException;
import org.ikigaidigital.application.port.in.UpdateAllBalancesUseCase;
import org.ikigaidigital.application.port.out.DistributedLock;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Decorator that lets only one balance update run at a time across all instances. When two calls overlap (same or
 * different pods) the second one fails fast with {@link BalanceUpdateInProgressException} instead of waiting - after
 * waiting it would credit a second month.
 * <p>
 * Deliberately NOT {@code @Transactional}: it calls the transactional {@link UpdateAllBalancesService} through its
 * Spring proxy, so the order is lock -> transaction -> update and the lock is released only after the new balances
 * are committed. {@code @Primary} makes this decorator the {@link UpdateAllBalancesUseCase} the web adapter gets.
 */
@Service
@Primary
@RequiredArgsConstructor
public class ExclusiveUpdateAllBalances implements UpdateAllBalancesUseCase {

    public static final String LOCK_NAME = "time-deposits:update-all-balances";

    private final UpdateAllBalancesService delegate;
    private final DistributedLock lock;

    @Override
    public void updateAllBalances() {
        if (!lock.runExclusively(LOCK_NAME, delegate::updateAllBalances)) {
            throw new BalanceUpdateInProgressException();
        }
    }
}
