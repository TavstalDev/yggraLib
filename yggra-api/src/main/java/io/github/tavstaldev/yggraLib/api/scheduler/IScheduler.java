package io.github.tavstaldev.yggraLib.api.scheduler;

import io.github.tavstaldev.yggraLib.api.models.YggraEntity;
import io.github.tavstaldev.yggraLib.api.models.YggraLocation;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface IScheduler {

    ITask run(Runnable runnable);
    ITask runLater(Runnable runnable, long delay);
    ITask runRepeating(Runnable runnable, long delay, long period);

    ITask run(Runnable runnable, YggraLocation context);
    ITask runLater(Runnable runnable, long delay, YggraLocation context);
    ITask runRepeating(Runnable runnable, long delay, long period, YggraLocation context);

    ITask run(Runnable runnable, YggraEntity context);
    ITask runLater(Runnable runnable, long delay, YggraEntity context);
    ITask runRepeating(Runnable runnable, long delay, long period, YggraEntity context);

    ITask runAsync(Runnable runnable);
    ITask runLaterAsync(Runnable runnable, long delay);
    ITask runRepeatingAsync(Runnable runnable, long delay, long period);
    <T> CompletableFuture<T> async(Supplier<T> supplier);
}