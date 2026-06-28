package io.github.tavstaldev.yggra.core.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PaperScheduler implements IScheduler {

    private final JavaPlugin plugin;

    public PaperScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ITask run(Runnable runnable) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        BukkitTask task = Bukkit.getScheduler().runTask(plugin, runnable);
        if (isYggraTask) {
            yggraTask.setPaperTask(task);
            return yggraTask;
        }
        return wrap(task);
    }

    @Override
    public ITask runLater(Runnable runnable, long delay) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        if (isYggraTask) {
            yggraTask.setPaperTask(task);
            return yggraTask;
        }
        return wrap(task);
    }

    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
        if (isYggraTask) {
            yggraTask.setPaperTask(task);
            return yggraTask;
        }
        return wrap(task);
    }

    @Override
    public ITask runAsync(Runnable runnable) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        if (isYggraTask) {
            yggraTask.setPaperTask(task);
            return yggraTask;
        }
        return wrap(task);
    }

    @Override
    public ITask runLaterAsync(Runnable runnable, long delay) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
        if (isYggraTask) {
            yggraTask.setPaperTask(task);
            return yggraTask;
        }
        return wrap(task);
    }

    @Override
    public ITask runRepeatingAsync(Runnable runnable, long delay, long period) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
        if (isYggraTask) {
            yggraTask.setPaperTask(task);
            return yggraTask;
        }
        return wrap(task);
    }

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

    @Override
    public ITask run(Runnable runnable, Location context) {
        return run(runnable);
    }

    @Override
    public ITask runLater(Runnable runnable, long delay, Location context) {
        return runLater(runnable, delay);
    }

    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period, Location context) {
        return runRepeating(runnable, delay, period);
    }

    @Override
    public ITask run(Runnable runnable, Entity context) {
        return run(runnable);
    }

    @Override
    public ITask runLater(Runnable runnable, long delay, Entity context) {
        return runLater(runnable, delay);
    }

    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period, Entity context) {
        return runRepeating(runnable, delay, period);
    }

    private ITask wrap(BukkitTask task) {
        return new ITask() {
            @Override
            public void cancel() {
                task.cancel();
            }

            @Override
            public boolean isCancelled() {
                return task.isCancelled();
            }
        };
    }
}