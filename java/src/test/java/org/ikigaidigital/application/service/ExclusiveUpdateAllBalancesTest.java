package org.ikigaidigital.application.service;

import org.ikigaidigital.application.BalanceUpdateInProgressException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ExclusiveUpdateAllBalancesTest {

    private final UpdateAllBalancesService delegate = mock(UpdateAllBalancesService.class);
    private final List<String> requestedLocks = new ArrayList<>();

    @Test
    void runsTheUpdateWhileHoldingTheLock() {
        ExclusiveUpdateAllBalances exclusive = new ExclusiveUpdateAllBalances(delegate, (lockName, task) -> {
            requestedLocks.add(lockName);
            task.run();
            return true;
        });

        exclusive.updateAllBalances();

        verify(delegate).updateAllBalances();
        assertThat(requestedLocks).containsExactly(ExclusiveUpdateAllBalances.LOCK_NAME);
    }

    @Test
    void failsFastWithoutUpdatingWhenTheLockIsHeldElsewhere() {
        ExclusiveUpdateAllBalances exclusive = new ExclusiveUpdateAllBalances(delegate, (lockName, task) -> false);

        assertThatThrownBy(exclusive::updateAllBalances).isInstanceOf(BalanceUpdateInProgressException.class);
        verify(delegate, never()).updateAllBalances();
    }
}
