package com.github.TavstalDev.yggraLib.api.cache.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.TavstalDev.yggraLib.api.cache.ExpiringEntry;
import com.github.TavstalDev.yggraLib.api.cache.ICache;
import com.github.TavstalDev.yggraLib.api.logger.YggraLogger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * In-memory cache implementation backed by Caffeine.
 * Entries expire after their configured TTL, with a maximum of 10,000 entries.
 */
public class MemoryCache implements ICache {
    private final YggraLogger logger;
    private final Cache<String, ExpiringEntry> cache;

    /**
     * Creates a MemoryCache with the given logger.
     *
     * @param logger The logger instance.
     */
    public MemoryCache(YggraLogger logger) {
        this.logger = logger;
        this.cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfter(new Expiry<String, ExpiringEntry>() {
                    @Override
                    public long expireAfterCreate(@NotNull String key, @NotNull ExpiringEntry value, long currentTime) {
                        return value.durationNanos;
                    }

                    @Override
                    public long expireAfterUpdate(@NotNull String key, @NotNull ExpiringEntry value, long currentTime, long currentDuration) {
                        return value.durationNanos;
                    }

                    @Override
                    public long expireAfterRead(@NotNull String key, @NotNull ExpiringEntry value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        ExpiringEntry result = cache.getIfPresent(key);
        if (result == null) {
            return null;
        }

        try {
            return clazz.cast(result.data);
        }
        catch (ClassCastException e) {
            logger.error("Failed to cast cached value for key: " + key, e);
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public <T> void put(String key, T value, long ttlSeconds) {
        long durationNanos = TimeUnit.SECONDS.toNanos(ttlSeconds);
        cache.put(key, new ExpiringEntry(value, durationNanos));
    }

    /** {@inheritDoc} */
    @Override
    public void evict(String key) {
        cache.invalidate(key);
    }

    /** {@inheritDoc} */
    @Override
    public void evictAll() {
        cache.invalidateAll();
    }
}
