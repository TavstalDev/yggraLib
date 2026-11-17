package com.github.TavstalDev.yggraLib.impl.vlegacy.scheduler;

import com.github.TavstalDev.yggraLib.api.models.YggraEntity;
import com.github.TavstalDev.yggraLib.api.models.YggraLocation;
import com.github.TavstalDev.yggraLib.api.scheduler.IScheduler;
import com.github.TavstalDev.yggraLib.api.scheduler.ITask;
import com.github.TavstalDev.yggraLib.api.scheduler.YggraTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Legacy implementation of PaperScheduler for older Minecraft versions.
 */
public class PaperScheduler_legacy implements IScheduler {

    private final JavaPlugin plugin;

    /**
     * Creates a new PaperScheduler_legacy instance.
     * @param plugin the plugin value
     */
    public PaperScheduler_legacy(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    /**
     * Schedules work using the run operation.
     * @param runnable the runnable value
     * @return the scheduled task or future
     */

    @Override
    public ITask run(Runnable runnable) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        BukkitTask task = Bukkit.getScheduler().runTask(plugin, runnable);
        if (isYggraTask) {
            yggraTask.setParentTask(task);
            return yggraTask;
        }
        return wrap(task);
    }
    /**
     * Schedules work using the runLater operation.
     * @param runnable the runnable value
     * @param delay the delay value
     * @return the scheduled task or future
     */

    @Override
    public ITask runLater(Runnable runnable, long delay) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        if (isYggraTask) {
            yggraTask.setParentTask(task);
            return yggraTask;
        }
        return wrap(task);
    }
    /**
     * Schedules work using the runRepeating operation.
     * @param runnable the runnable value
     * @param delay the delay value
     * @param period the period value
     * @return the scheduled task or future
     */

    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
        if (isYggraTask) {
            yggraTask.setParentTask(task);
            return yggraTask;
        }
        return wrap(task);
    }
    /**
     * Schedules work using the runAsync operation.
     * @param runnable the runnable value
     * @return the scheduled task or future
     */

    @Override
    public ITask runAsync(Runnable runnable) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        if (isYggraTask) {
            yggraTask.setParentTask(task);
            return yggraTask;
        }
        return wrap(task);
    }
    /**
     * Schedules work using the runLaterAsync operation.
     * @param runnable the runnable value
     * @param delay the delay value
     * @return the scheduled task or future
     */

    @Override
    public ITask runLaterAsync(Runnable runnable, long delay) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
        if (isYggraTask) {
            yggraTask.setParentTask(task);
            return yggraTask;
        }
        return wrap(task);
    }
    /**
     * Schedules work using the runRepeatingAsync operation.
     * @param runnable the runnable value
     * @param delay the delay value
     * @param period the period value
     * @return the scheduled task or future
     */

    @Override
    public ITask runRepeatingAsync(Runnable runnable, long delay, long period) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
        if (isYggraTask) {
            yggraTask.setParentTask(task);
            return yggraTask;
        }
        return wrap(task);
    }
    /**
     * Schedules work using the async operation.
     * @param task the task value
     * @return the scheduled task or future
     */

    @Override
    public <T> CompletableFuture<T> async(Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                future.complete(task.get());
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });

        return future;
    }
    /**
     * Schedules work using the run operation.
     * @param runnable the runnable value
     * @param context the context value
     * @return the scheduled task or future
     */

    @Override
    public ITask run(Runnable runnable, YggraLocation context) {
        return run(runnable);
    }
    /**
     * Schedules work using the runLater operation.
     * @param runnable the runnable value
     * @param delay the delay value
     * @param context the context value
     * @return the scheduled task or future
     */

    @Override
    public ITask runLater(Runnable runnable, long delay, YggraLocation context) {
        return runLater(runnable, delay);
    }
    /**
     * Schedules work using the runRepeating operation.
     * @param runnable the runnable value
     * @param delay the delay value
     * @param period the period value
     * @param context the context value
     * @return the scheduled task or future
     */

    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period, YggraLocation context) {
        return runRepeating(runnable, delay, period);
    }
    /**
     * Schedules work using the run operation.
     * @param runnable the runnable value
     * @param context the context value
     * @return the scheduled task or future
     */

    @Override
    public ITask run(Runnable runnable, YggraEntity context) {
        return run(runnable);
    }
    /**
     * Schedules work using the runLater operation.
     * @param runnable the runnable value
     * @param delay the delay value
     * @param context the context value
     * @return the scheduled task or future
     */

    @Override
    public ITask runLater(Runnable runnable, long delay, YggraEntity context) {
        return runLater(runnable, delay);
    }
    /**
     * Schedules work using the runRepeating operation.
     * @param runnable the runnable value
     * @param delay the delay value
     * @param period the period value
     * @param context the context value
     * @return the scheduled task or future
     */

    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period, YggraEntity context) {
        return runRepeating(runnable, delay, period);
    }

    private ITask wrap(BukkitTask task) {
        return new ITask() {
            private boolean cancelled = false;
            /**
             * Handles the cancel operation.
             */

            @Override
            public void cancel() {
                task.cancel();
                cancelled = true;
            }
            /**
             * Returns the value for isCancelled.
             * @return the result
             */

            @Override
            public boolean isCancelled() {
                return cancelled;
            }
        };
    }
}