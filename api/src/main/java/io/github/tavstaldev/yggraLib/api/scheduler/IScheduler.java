package io.github.tavstaldev.yggraLib.api.scheduler;

import io.github.tavstaldev.yggraLib.api.models.YggraEntity;
import io.github.tavstaldev.yggraLib.api.models.YggraLocation;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Provides methods to run tasks on the server thread, in a specific context, or asynchronously.
 * Time values such as delay and period are given in ticks unless stated otherwise by the implementation.
 */
public interface IScheduler {

    /**
     * Runs a task as soon as possible.
     *
     * @param runnable the task to run
     * @return the created task
     */
    ITask run(Runnable runnable);

    /**
     * Runs a task after a delay.
     *
     * @param runnable the task to run
     * @param delay    the delay before execution
     * @return the created task
     */
    ITask runLater(Runnable runnable, long delay);

    /**
     * Runs a task repeatedly with a delay and a period.
     *
     * @param runnable the task to run
     * @param delay    the delay before the first run
     * @param period   the time between runs
     * @return the created task
     */
    ITask runRepeating(Runnable runnable, long delay, long period);

    /**
     * Runs a task in the context of a location.
     *
     * @param runnable the task to run
     * @param context  the location context
     * @return the created task
     */
    ITask run(Runnable runnable, YggraLocation context);

    /**
     * Runs a task later in the context of a location.
     *
     * @param runnable the task to run
     * @param delay    the delay before execution
     * @param context  the location context
     * @return the created task
     */
    ITask runLater(Runnable runnable, long delay, YggraLocation context);

    /**
     * Runs a repeating task in the context of a location.
     *
     * @param runnable the task to run
     * @param delay    the delay before the first run
     * @param period   the time between runs
     * @param context  the location context
     * @return the created task
     */
    ITask runRepeating(Runnable runnable, long delay, long period, YggraLocation context);

    /**
     * Runs a task in the context of an entity.
     *
     * @param runnable the task to run
     * @param context  the entity context
     * @return the created task
     */
    ITask run(Runnable runnable, YggraEntity context);

    /**
     * Runs a task later in the context of an entity.
     *
     * @param runnable the task to run
     * @param delay    the delay before execution
     * @param context  the entity context
     * @return the created task
     */
    ITask runLater(Runnable runnable, long delay, YggraEntity context);

    /**
     * Runs a repeating task in the context of an entity.
     *
     * @param runnable the task to run
     * @param delay    the delay before the first run
     * @param period   the time between runs
     * @param context  the entity context
     * @return the created task
     */
    ITask runRepeating(Runnable runnable, long delay, long period, YggraEntity context);

    /**
     * Runs a task asynchronously.
     *
     * @param runnable the task to run
     * @return the created task
     */
    ITask runAsync(Runnable runnable);

    /**
     * Runs a task asynchronously after a delay.
     *
     * @param runnable the task to run
     * @param delay    the delay before execution
     * @return the created task
     */
    ITask runLaterAsync(Runnable runnable, long delay);

    /**
     * Runs a task asynchronously on a repeating schedule.
     *
     * @param runnable the task to run
     * @param delay    the delay before the first run
     * @param period   the time between runs
     * @return the created task
     */
    ITask runRepeatingAsync(Runnable runnable, long delay, long period);

    /**
     * Runs a supplier asynchronously and returns its result as a future.
     *
     * @param supplier the code that produces a value
     * @param <T>      the result type
     * @return a future that completes with the supplier result
     */
    <T> CompletableFuture<T> async(Supplier<T> supplier);
}