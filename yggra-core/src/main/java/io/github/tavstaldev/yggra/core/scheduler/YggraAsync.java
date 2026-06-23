package io.github.tavstaldev.yggra.core.scheduler;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class YggraAsync {

    private final IScheduler scheduler;

    public YggraAsync(IScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public <T> CompletableFuture<T> async(Supplier<T> supplier) {
        return scheduler.async(supplier);
    }

    public <T> void sync(Consumer<T> consumer, T value) {
        scheduler.run(() -> consumer.accept(value));
    }
}
