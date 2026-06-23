package io.github.tavstaldev.yggra.core.scheduler;

import org.bukkit.Bukkit;
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
        BukkitTask task = Bukkit.getScheduler().runTask(plugin, runnable);
        return wrap(task);
    }

    @Override
    public ITask runAsync(Runnable runnable) {
        BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        return wrap(task);
    }

    @Override
    public ITask runLater(Runnable runnable, long delay) {
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        return wrap(task);
    }

    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
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

    private ITask wrap(BukkitTask task) {
        return new ITask() {
            @Override
            public void cancel() {
                task.cancel();
            }

            @Override
            public boolean isActive() {
                return !task.isCancelled();
            }
        };
    }
}