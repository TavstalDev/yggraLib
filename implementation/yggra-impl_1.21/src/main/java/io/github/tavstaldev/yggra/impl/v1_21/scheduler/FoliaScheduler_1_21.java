package io.github.tavstaldev.yggra.impl.v1_21.scheduler;

import io.github.tavstaldev.yggra.api.models.YggraEntity;
import io.github.tavstaldev.yggra.api.models.YggraLocation;
import io.github.tavstaldev.yggra.api.scheduler.IScheduler;
import io.github.tavstaldev.yggra.api.scheduler.ITask;
import io.github.tavstaldev.yggra.api.scheduler.YggraTask;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class FoliaScheduler_1_21 implements IScheduler {

    private final JavaPlugin plugin;

    public FoliaScheduler_1_21(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ITask run(Runnable runnable) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;

        var scheduledTask = Bukkit.getGlobalRegionScheduler().run(plugin, t -> runnable.run());
        if (isYggraTask) {
            yggraTask.setParentTask(scheduledTask);
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
            yggraTask.setParentTask(scheduledTask);
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
            yggraTask.setParentTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask run(Runnable runnable, YggraLocation context) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;
        World world = Bukkit.getWorld(context.getWorldName());
        Location location = new Location(world, context.getX(), context.getY(), context.getZ(), context.getYaw(), context.getPitch());

        var scheduledTask = Bukkit.getRegionScheduler().run(plugin, location, t -> runnable.run());
        if (isYggraTask) {
            yggraTask.setParentTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runLater(Runnable runnable, long delay, YggraLocation context) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;
        World world = Bukkit.getWorld(context.getWorldName());
        Location location = new Location(world, context.getX(), context.getY(), context.getZ(), context.getYaw(), context.getPitch());

        var scheduledTask = Bukkit.getRegionScheduler().runDelayed(plugin, location, t -> runnable.run(), delay);
        if (isYggraTask) {
            yggraTask.setParentTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period, YggraLocation context) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;
        World world = Bukkit.getWorld(context.getWorldName());
        Location location = new Location(world, context.getX(), context.getY(), context.getZ(), context.getYaw(), context.getPitch());

        var scheduledTask = Bukkit.getRegionScheduler()
                .runAtFixedRate(plugin, location, t -> runnable.run(), delay, period);
        if (isYggraTask) {
            yggraTask.setParentTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask run(Runnable runnable, YggraEntity context) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;
        Entity entity = Bukkit.getEntity(context.getUniqueId());
        if (entity == null)
            return null;

        var scheduledTask = entity.getScheduler().run(plugin, t -> runnable.run(), () -> {});
        if (isYggraTask) {
            yggraTask.setParentTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runLater(Runnable runnable, long delay, YggraEntity context) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;
        Entity entity = Bukkit.getEntity(context.getUniqueId());
        if (entity == null)
            return null;

        var scheduledTask = entity.getScheduler().runDelayed(plugin, t -> runnable.run(), () -> {}, delay);
        if (isYggraTask) {
            yggraTask.setParentTask(scheduledTask);
            return yggraTask;
        }
        return simpleTask(scheduledTask);
    }

    @Override
    public ITask runRepeating(Runnable runnable, long delay, long period, YggraEntity context) {
        boolean isYggraTask = runnable instanceof YggraTask;
        YggraTask yggraTask = isYggraTask ? (YggraTask)runnable : null;
        Entity entity = Bukkit.getEntity(context.getUniqueId());
        if (entity == null)
            return null;

        var scheduledTask = entity.getScheduler()
                .runAtFixedRate(plugin, t -> runnable.run(), () -> {}, delay, period);
        if (isYggraTask) {
            yggraTask.setParentTask(scheduledTask);
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
            yggraTask.setParentTask(scheduledTask);
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
            yggraTask.setParentTask(scheduledTask);
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
            yggraTask.setParentTask(scheduledTask);
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
