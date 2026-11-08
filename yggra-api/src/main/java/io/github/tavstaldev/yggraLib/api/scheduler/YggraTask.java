package io.github.tavstaldev.yggraLib.api.scheduler;

import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public abstract class YggraTask implements Runnable, ITask {
    private boolean cancelled = false;
    @Setter
    private @Nullable Object parentTask = null;

    @Override
    public abstract void run();

    @Override
    public final void cancel() {
        this.cancelled = true;
        if (parentTask == null)
            return;
        try {
            Method method = parentTask.getClass().getMethod("cancel");
            method.invoke(parentTask);
        } catch (Exception ignored) {

        }
    }

    @Override
    public final boolean isCancelled() {
        if (parentTask == null)
            return cancelled;
        try {
            Method method = parentTask.getClass().getMethod("isCancelled");
            return (Boolean)method.invoke(parentTask);
        } catch (Exception ignored) {
            return false;
        }
    }
}
