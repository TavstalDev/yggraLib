package io.github.tavstaldev.yggraLib.api.scheduler;

import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Base class for tasks created by YggraLib schedulers.
 * It can also forward cancel and state checks to a platform-specific parent task.
 */
public abstract class YggraTask implements Runnable, ITask {
    private boolean cancelled = false;
    @Setter
    private @Nullable Object parentTask = null;

    /**
     * Runs the task logic.
     */
    @Override
    public abstract void run();

    /**
     * Cancels this task and, if available, the underlying parent task.
     */
    @Override
    public final void cancel() {
        this.cancelled = true;
        if (parentTask == null)
            return;
        try {
            Method method = parentTask.getClass().getMethod("cancel");
            method.invoke(parentTask);
        } catch (Exception ignored) {
            // Ignore reflection errors and keep the local cancelled state.
        }
    }

    /**
     * Checks if this task is cancelled.
     * If a parent task exists, its cancelled state is used.
     *
     * @return {@code true} if the task is cancelled, otherwise {@code false}
     */
    @Override
    public final boolean isCancelled() {
        if (parentTask == null)
            return cancelled;
        try {
            Method method = parentTask.getClass().getMethod("isCancelled");
            return (Boolean) method.invoke(parentTask);
        } catch (Exception ignored) {
            return false;
        }
    }
}
