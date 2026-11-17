package com.github.TavstalDev.yggraLib.api.cache;

/**
 * Stores a cached value together with its expiration time.
 */
public class ExpiringEntry {
    /** The cached value. */
    public final Object data;
    /** The time until expiration, in nanoseconds. */
    public final long durationNanos;

    /**
     * Creates a new cache entry.
     *
     * @param data          the value to store
     * @param durationNanos the time until expiration, in nanoseconds
     */
    public ExpiringEntry(Object data, long durationNanos) {
        this.data = data;
        this.durationNanos = durationNanos;
    }
}
