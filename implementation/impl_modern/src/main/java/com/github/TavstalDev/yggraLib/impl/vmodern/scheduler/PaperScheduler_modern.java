package com.github.TavstalDev.yggraLib.impl.vmodern.scheduler;

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
 * Modern implementation of PaperScheduler for newer Minecraft versions.
 */
public class PaperScheduler_modern implements IScheduler {

    private final JavaPlugin plugin;

    /**
     * Creates a new PaperScheduler_modern instance.
     * @param plugin the plugin value
     */
    public PaperScheduler_modern(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    /** {@inheritDoc} */
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
    /** {@inheritDoc} */
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
    /** {@inheritDoc} */
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
    /** {@inheritDoc} */
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
    /** {@inheritDoc} */
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
    /** {@inheritDoc} */
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
    /** {@inheritDoc} */
    @Override
    public <T> CompletableFuture<T> async(Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();

        Bukkit.getAsyncScheduler().runNow(plugin, scheduled -> {
            try {
                future.complete(task.get());
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });

        return future;
    }
    /** {@inheritDoc} */
    @Override
    public ITask run(Runnable runnable, YggraLocation context) {
        return run(runnable);
    }
    /** {@inheritDoc} */
    @Override
    public ITask runLater(Runnable runnable, long delay, YggraLocation context) {
        return runLater(runnable, delay);
    }
    /** {@inheritDoc} */
    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period, YggraLocation context) {
        return runRepeating(runnable, delay, period);
    }
    /** {@inheritDoc} */
    @Override
    public ITask run(Runnable runnable, YggraEntity context) {
        return run(runnable);
    }
    /** {@inheritDoc} */
    @Override
    public ITask runLater(Runnable runnable, long delay, YggraEntity context) {
        return runLater(runnable, delay);
    }
    /** {@inheritDoc} */
    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period, YggraEntity context) {
        return runRepeating(runnable, delay, period);
    }

    private ITask wrap(BukkitTask task) {
        return new ITask() {
            /** {@inheritDoc} */
            @Override
            public void cancel() {
                task.cancel();
            }
            /** {@inheritDoc} */
            @Override
            public boolean isCancelled() {
                return task.isCancelled();
            }
        };
    }
}