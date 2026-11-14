package io.github.tavstaldev.yggraLib.api.cache;

import org.jetbrains.annotations.Nullable;

/**
 * Defines a generic cache interface with TTL-based expiration.
 */
public interface ICache {

    /**
     * Retrieves a cached value by key.
     *
     * @param key   The cache key.
     * @param clazz The expected type of the cached value.
     * @param <T>   The value type.
     * @return The cached value, or null if not found or expired.
     */
    <T> @Nullable T get(String key, Class<T> clazz);

    /**
     * Stores a value in the cache with a time-to-live.
     *
     * @param key        The cache key.
     * @param value      The value to cache.
     * @param ttlSeconds The time-to-live in seconds.
     * @param <T>        The value type.
     */
    <T> void put(String key, T value, long ttlSeconds);

    /**
     * Removes a value from the cache.
     *
     * @param key The cache key to evict.
     */
    void evict(String key);
}
