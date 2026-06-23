package io.github.tavstaldev.yggra.core.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class FoliaScheduler implements IScheduler {

    private final JavaPlugin plugin;

    public FoliaScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ITask run(Runnable task) {
        Bukkit.getGlobalRegionScheduler().execute(plugin, task);
        return simpleTask();
    }

    @Override
    public ITask runAsync(Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().run(plugin, task -> runnable.run());
        return simpleTask();
    }

    @Override
    public ITask runLater(Runnable task, long delay) {
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> task.run(), delay);
        return simpleTask();
    }

    @Override
    public ITask runRepeating(Runnable task, long delay, long period) {
        Bukkit.getGlobalRegionScheduler()
                .runAtFixedRate(plugin, t -> task.run(), delay, period);
        return simpleTask();
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

    private ITask simpleTask() {
        return new ITask() {
            @Override public void cancel() {}
            @Override public boolean isActive() { return true; }
        };
    }
}
