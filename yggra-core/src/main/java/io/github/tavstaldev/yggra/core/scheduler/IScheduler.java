package io.github.tavstaldev.yggra.core.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface IScheduler {

    ITask run(Runnable runnable);
    ITask runLater(Runnable runnable, long delay);
    ITask runRepeating(Runnable runnable, long delay, long period);

    ITask run(Runnable runnable, Location context);
    ITask runLater(Runnable runnable, long delay, Location context);
    ITask runRepeating(Runnable runnable, long delay, long period, Location context);

    ITask run(Runnable runnable, Entity context);
    ITask runLater(Runnable runnable, long delay, Entity context);
    ITask runRepeating(Runnable runnable, long delay, long period, Entity context);

    ITask runAsync(Runnable runnable);
    ITask runLaterAsync(Runnable runnable, long delay);
    ITask runRepeatingAsync(Runnable runnable, long delay, long period);
    <T> CompletableFuture<T> async(Supplier<T> supplier);
}