package io.github.tavstaldev.yggra.core.scheduler;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface IScheduler {

    ITask run(Runnable runnable);

    ITask runAsync(Runnable runnable);

    ITask runLater(Runnable runnable, long delay);

    ITask runRepeating(
            Runnable runnable,
            long delay,
            long period
    );

    <T> CompletableFuture<T> async(Supplier<T> supplier);
}