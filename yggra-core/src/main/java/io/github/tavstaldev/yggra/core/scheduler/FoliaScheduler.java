package io.github.tavstaldev.yggra.core.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class FoliaScheduler implements IScheduler {

    private final JavaPlugin plugin;

    public FoliaScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ITask run(Runnable runnable) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = Bukkit.getGlobalRegionScheduler().run(plugin, t -> runnable.run());
        if (isYggraTask) {
            yggraTask.setFoliaTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runLater(Runnable runnable, long delay) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> runnable.run(), delay);
        if (isYggraTask) {
            yggraTask.setFoliaTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = Bukkit.getGlobalRegionScheduler()
                .runAtFixedRate(plugin, t -> runnable.run(), delay, period);
        if (isYggraTask) {
            yggraTask.setFoliaTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask run(Runnable runnable, Location context) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = Bukkit.getRegionScheduler().run(plugin, context, t -> runnable.run());
        if (isYggraTask) {
            yggraTask.setFoliaTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runLater(Runnable runnable, long delay, Location context) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = Bukkit.getRegionScheduler().runDelayed(plugin, context, t -> runnable.run(), delay);
        if (isYggraTask) {
            yggraTask.setFoliaTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period, Location context) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = Bukkit.getRegionScheduler()
                .runAtFixedRate(plugin, context, t -> runnable.run(), delay, period);
        if (isYggraTask) {
            yggraTask.setFoliaTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask run(Runnable runnable, Entity context) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = context.getScheduler().run(plugin, t -> runnable.run(), () -> {});
        if (isYggraTask) {
            yggraTask.setFoliaTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runLater(Runnable runnable, long delay, Entity context) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = context.getScheduler().runDelayed(plugin, t -> runnable.run(), () -> {}, delay);
        if (isYggraTask) {
            yggraTask.setFoliaTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period, Entity context) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = context.getScheduler()
                .runAtFixedRate(plugin, t -> runnable.run(), () -> {}, delay, period);
        if (isYggraTask) {
            yggraTask.setFoliaTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runAsync(Runnable runnable) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = Bukkit.getAsyncScheduler().runNow(plugin, task -> runnable.run());
        if (isYggraTask) {
            yggraTask.setFoliaTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runLaterAsync(Runnable runnable, long delay) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = Bukkit.getAsyncScheduler().runDelayed(plugin, task -> runnable.run(),
                delay / 20L, TimeUnit.SECONDS);
        if (isYggraTask) {
            yggraTask.setFoliaTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runRepeatingAsync(Runnable runnable, long delay, long period) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> runnable.run(),
                delay / 20L,
                period / 20L, TimeUnit.SECONDS);
        if (isYggraTask) {
            yggraTask.setFoliaTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
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

    private ITask simpleTask(ScheduledTask task) {
        return new ITask() {
            @Override public void cancel() {
                task.cancel();
            }
            @Override public boolean isCancelled() {
                return task.isCancelled();
            }
        };
    }
}
