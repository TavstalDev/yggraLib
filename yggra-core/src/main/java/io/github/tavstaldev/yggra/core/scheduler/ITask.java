package io.github.tavstaldev.yggra.core.scheduler;

public interface ITask {

    void cancel();

    boolean isActive();
}
