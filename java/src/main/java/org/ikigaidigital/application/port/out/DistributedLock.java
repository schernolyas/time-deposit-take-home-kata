package org.ikigaidigital.application.port.out;

/** Cluster-wide mutual exclusion across all application instances (e.g. several Kubernetes pods). */
public interface DistributedLock {

    /**
     * Runs the task while holding the named lock. Does not wait: if another instance holds the lock, the task is
     * not run.
     *
     * @return {@code true} if the task was run, {@code false} if the lock is held elsewhere
     */
    boolean runExclusively(String lockName, Runnable task);
}
