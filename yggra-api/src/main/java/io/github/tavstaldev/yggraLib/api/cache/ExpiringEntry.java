package io.github.tavstaldev.yggraLib.api.cache;

public class ExpiringEntry {
    public Object data;

    public long durationNanos;

    public ExpiringEntry(Object data, long durationNanos) {
        this.data = data;
        this.durationNanos = durationNanos;
    }
}
