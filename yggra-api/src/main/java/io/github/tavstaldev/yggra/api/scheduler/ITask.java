package io.github.tavstaldev.yggra.api.scheduler;

public interface ITask {

    void cancel();

    boolean isCancelled();
}
