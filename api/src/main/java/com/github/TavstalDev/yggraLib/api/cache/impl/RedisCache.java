package com.github.TavstalDev.yggraLib.api.cache.impl;

import com.google.gson.Gson;
import com.github.TavstalDev.yggraLib.api.cache.ICache;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.params.SetParams;

import java.util.HashSet;

/**
 * Redis-backed cache implementation. Values are serialized to JSON via Gson and stored
 * with TTL-based expiration using Redis' EX command.
 */
@SuppressWarnings("unused")
public class RedisCache implements ICache {
    private final RedisClient redisClient;
    private final Gson gson = new Gson();
    private HashSet<String> managedKeys = new HashSet<>();

    /**
     * Creates a RedisCache connecting to the specified Redis server.
     *
     * @param host     The Redis server hostname.
     * @param port     The Redis server port.
     * @param username The Redis username.
     * @param password The Redis password.
     */
    public RedisCache(String host, int port, String username, String password) {
        redis.clients.jedis.JedisClientConfig config = DefaultJedisClientConfig.builder()
                .user(username)
                .password(password)
                .timeoutMillis(3000)
                .build();

        this.redisClient = RedisClient.builder()
                .hostAndPort(new HostAndPort(host, port))
                .clientConfig(config)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> @Nullable T get(String key, Class<T> clazz) {
        String json = redisClient.get(key);
        if (json == null)
            return null;

        if (clazz == String.class)
            return clazz.cast(json);
        return gson.fromJson(json, clazz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void put(String key, T value, long ttlSeconds) {
        String dataToStore;
        if (value instanceof String)
            dataToStore = (String)value;
        else
            dataToStore = gson.toJson(value);

        SetParams setParams = new SetParams().ex(ttlSeconds);
        redisClient.set(key, dataToStore, setParams);
        managedKeys.add(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evict(String key) {

        redisClient.del(key);
        managedKeys.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictAll() {
        redisClient.del(managedKeys.toArray(new String[0]));
        managedKeys.clear();
    }
}
