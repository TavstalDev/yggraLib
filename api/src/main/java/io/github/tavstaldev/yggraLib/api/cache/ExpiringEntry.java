package io.github.tavstaldev.yggraLib.api.cache;

public class ExpiringEntry {
    public final Object data;
    public final long durationNanos;

    public ExpiringEntry(Object data, long durationNanos) {
        this.data = data;
        this.durationNanos = durationNanos;
    }
}
