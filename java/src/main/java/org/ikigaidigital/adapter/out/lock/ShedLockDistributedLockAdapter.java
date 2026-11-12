package org.ikigaidigital.adapter.out.lock;

import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.ikigaidigital.application.port.out.DistributedLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.Instant;

/**
 * {@link DistributedLock} on ShedLock with the {@code shedlock} table in the shared PostgreSQL database.
 * <p>
 * ShedLock locks are time-bound: if an instance dies while holding the lock, it stays taken until
 * {@code lockAtMostFor} expires, so that value must exceed the longest expected balance update.
 * Database time is used for lock timestamps, so clock skew between pods does not matter.
 */
@Component
class ShedLockDistributedLockAdapter implements DistributedLock {

    private final LockingTaskExecutor executor;
    private final Duration lockAtMostFor;

    ShedLockDistributedLockAdapter(DataSource dataSource,
                                   @Value("${time-deposit.lock.at-most-for:PT10M}") Duration lockAtMostFor) {
        this.executor = new DefaultLockingTaskExecutor(new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()
                        .build()));
        this.lockAtMostFor = lockAtMostFor;
    }

    @Override
    public boolean runExclusively(String lockName, Runnable task) {
        LockConfiguration configuration = new LockConfiguration(Instant.now(), lockName, lockAtMostFor, Duration.ZERO);
        try {
            return executor.executeWithLock((LockingTaskExecutor.TaskWithResult<Void>) () -> {
                task.run();
                return null;
            }, configuration).wasExecuted();
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException("Task under lock '" + lockName + "' failed", t);
        }
    }
}
