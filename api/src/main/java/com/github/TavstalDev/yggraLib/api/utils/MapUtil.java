package com.github.TavstalDev.yggraLib.api.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for creating mutable maps.
 */
@SuppressWarnings("unused")
public class MapUtil {

    /**
     * Creates a new map with one key-value pair.
     *
     * @param key1   the key
     * @param value1 the value
     * @param <K>    the key type
     * @param <V>    the value type
     * @return a new map containing the provided entry
     */
    public static <K, V> Map<K, V> of(K key1, V value1) {
        Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        return result;
    }

    /**
     * Creates a new map with two key-value pairs.
     *
     * @param key1   the first key
     * @param value1 the first value
     * @param key2   the second key
     * @param value2 the second value
     * @param <K>    the key type
     * @param <V>    the value type
     * @return a new map containing the provided entries
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2) {
        Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        return result;
    }

    /**
     * Creates a new map with three key-value pairs.
     *
     * @param key1   the first key
     * @param value1 the first value
     * @param key2   the second key
     * @param value2 the second value
     * @param key3   the third key
     * @param value3 the third value
     * @param <K>    the key type
     * @param <V>    the value type
     * @return a new map containing the provided entries
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3) {
        Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        result.put(key3, value3);
        return result;
    }

    /**
     * Creates a new map with four key-value pairs.
     *
     * @param key1   the first key
     * @param value1 the first value
     * @param key2   the second key
     * @param value2 the second value
     * @param key3   the third key
     * @param value3 the third value
     * @param key4   the fourth key
     * @param value4 the fourth value
     * @param <K>    the key type
     * @param <V>    the value type
     * @return a new map containing the provided entries
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3,
                                      K key4, V value4) {
        Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        result.put(key3, value3);
        result.put(key4, value4);
        return result;
    }

    /**
     * Creates a new map with five key-value pairs.
     *
     * @param key1   the first key
     * @param value1 the first value
     * @param key2   the second key
     * @param value2 the second value
     * @param key3   the third key
     * @param value3 the third value
     * @param key4   the fourth key
     * @param value4 the fourth value
     * @param key5   the fifth key
     * @param value5 the fifth value
     * @param <K>    the key type
     * @param <V>    the value type
     * @return a new map containing the provided entries
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3,
                                      K key4, V value4, K key5, V value5) {
        Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        result.put(key3, value3);
        result.put(key4, value4);
        result.put(key5, value5);
        return result;
    }

    /**
     * Creates a new map with six key-value pairs.
     *
     * @param key1   the first key
     * @param value1 the first value
     * @param key2   the second key
     * @param value2 the second value
     * @param key3   the third key
     * @param value3 the third value
     * @param key4   the fourth key
     * @param value4 the fourth value
     * @param key5   the fifth key
     * @param value5 the fifth value
     * @param key6   the sixth key
     * @param value6 the sixth value
     * @param <K>    the key type
     * @param <V>    the value type
     * @return a new map containing the provided entries
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3,
                                      K key4, V value4, K key5, V value5, K key6, V value6) {
        Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        result.put(key3, value3);
        result.put(key4, value4);
        result.put(key5, value5);
        result.put(key6, value6);
        return result;
    }

    /**
     * Creates a new map with seven key-value pairs.
     *
     * @param key1   the first key
     * @param value1 the first value
     * @param key2   the second key
     * @param value2 the second value
     * @param key3   the third key
     * @param value3 the third value
     * @param key4   the fourth key
     * @param value4 the fourth value
     * @param key5   the fifth key
     * @param value5 the fifth value
     * @param key6   the sixth key
     * @param value6 the sixth value
     * @param key7   the seventh key
     * @param value7 the seventh value
     * @param <K>    the key type
     * @param <V>    the value type
     * @return a new map containing the provided entries
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3,
                                      K key4, V value4, K key5, V value5, K key6, V value6,
                                      K key7, V value7) {
        Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        result.put(key3, value3);
        result.put(key4, value4);
        result.put(key5, value5);
        result.put(key6, value6);
        result.put(key7, value7);
        return result;
    }

    /**
     * Creates a new map with eight key-value pairs.
     *
     * @param key1   the first key
     * @param value1 the first value
     * @param key2   the second key
     * @param value2 the second value
     * @param key3   the third key
     * @param value3 the third value
     * @param key4   the fourth key
     * @param value4 the fourth value
     * @param key5   the fifth key
     * @param value5 the fifth value
     * @param key6   the sixth key
     * @param value6 the sixth value
     * @param key7   the seventh key
     * @param value7 the seventh value
     * @param key8   the eighth key
     * @param value8 the eighth value
     * @param <K>    the key type
     * @param <V>    the value type
     * @return a new map containing the provided entries
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3,
                                      K key4, V value4, K key5, V value5, K key6, V value6,
                                      K key7, V value7, K key8, V value8) {
        Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        result.put(key3, value3);
        result.put(key4, value4);
        result.put(key5, value5);
        result.put(key6, value6);
        result.put(key7, value7);
        result.put(key8, value8);
        return result;
    }

    /**
     * Creates a new map with nine key-value pairs.
     *
     * @param key1   the first key
     * @param value1 the first value
     * @param key2   the second key
     * @param value2 the second value
     * @param key3   the third key
     * @param value3 the third value
     * @param key4   the fourth key
     * @param value4 the fourth value
     * @param key5   the fifth key
     * @param value5 the fifth value
     * @param key6   the sixth key
     * @param value6 the sixth value
     * @param key7   the seventh key
     * @param value7 the seventh value
     * @param key8   the eighth key
     * @param value8 the eighth value
     * @param key9   the ninth key
     * @param value9 the ninth value
     * @param <K>    the key type
     * @param <V>    the value type
     * @return a new map containing the provided entries
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3,
                                      K key4, V value4, K key5, V value5, K key6, V value6,
                                      K key7, V value7, K key8, V value8, K key9, V value9) {
        Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        result.put(key3, value3);
        result.put(key4, value4);
        result.put(key5, value5);
        result.put(key6, value6);
        result.put(key7, value7);
        result.put(key8, value8);
        result.put(key9, value9);
        return result;
    }

    /**
     * Creates a new map with ten key-value pairs.
     *
     * @param key1    the first key
     * @param value1  the first value
     * @param key2    the second key
     * @param value2  the second value
     * @param key3    the third key
     * @param value3  the third value
     * @param key4    the fourth key
     * @param value4  the fourth value
     * @param key5    the fifth key
     * @param value5  the fifth value
     * @param key6    the sixth key
     * @param value6  the sixth value
     * @param key7    the seventh key
     * @param value7  the seventh value
     * @param key8    the eighth key
     * @param value8  the eighth value
     * @param key9    the ninth key
     * @param value9  the ninth value
     * @param key10   the tenth key
     * @param value10 the tenth value
     * @param <K>     the key type
     * @param <V>     the value type
     * @return a new map containing the provided entries
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3,
                                      K key4, V value4, K key5, V value5, K key6, V value6,
                                      K key7, V value7, K key8, V value8, K key9, V value9,
                                      K key10, V value10) {
        Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        result.put(key3, value3);
        result.put(key4, value4);
        result.put(key5, value5);
        result.put(key6, value6);
        result.put(key7, value7);
        result.put(key8, value8);
        result.put(key9, value9);
        result.put(key10, value10);
        return result;
    }
}
