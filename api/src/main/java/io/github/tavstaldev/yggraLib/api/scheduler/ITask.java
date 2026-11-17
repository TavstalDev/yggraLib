package io.github.tavstaldev.yggraLib.api.scheduler;

/**
 * Represents a scheduled task that can be cancelled.
 */
public interface ITask {

    /**
     * Cancels this task.
     */
    void cancel();

    /**
     * Checks if this task has been cancelled.
     *
     * @return {@code true} if the task is cancelled, otherwise {@code false}
     */
    boolean isCancelled();
}
