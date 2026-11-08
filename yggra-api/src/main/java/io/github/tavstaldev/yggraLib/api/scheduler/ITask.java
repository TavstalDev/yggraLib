package io.github.tavstaldev.yggraLib.api.scheduler;

public interface ITask {

    void cancel();

    boolean isCancelled();
}
