package com.github.TavstalDev.yggraLib.bukkit.items;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Provides version-safe access to NBT data stored on {@link ItemStack}s.
 * <p>
 * Supports reading and writing primitive values, arrays, and retrieving all
 * stored entries as a map.
 */
@SuppressWarnings("unused")
public interface INbtAccessor {

    /**
     * Gets a string value from the item's NBT data.
     *
     * @param item the item to read from
     * @param key  the NBT key
     * @return the value, or {@code null} if absent or not a string
     */
    @Nullable String getString(ItemStack item, String key);

    /**
     * Gets an integer value from the item's NBT data.
     *
     * @param item the item to read from
     * @param key  the NBT key
     * @return the value, or {@code null} if absent or not an integer
     */
    @Nullable Integer getInteger(ItemStack item, String key);

    /**
     * Gets a double value from the item's NBT data.
     *
     * @param item the item to read from
     * @param key  the NBT key
     * @return the value, or {@code null} if absent or not a double
     */
    @Nullable Double getDouble(ItemStack item, String key);

    /**
     * Gets a float value from the item's NBT data.
     *
     * @param item the item to read from
     * @param key  the NBT key
     * @return the value, or {@code null} if absent or not a float
     */
    @Nullable Float getFloat(ItemStack item, String key);

    /**
     * Gets a long value from the item's NBT data.
     *
     * @param item the item to read from
     * @param key  the NBT key
     * @return the value, or {@code null} if absent or not a long
     */
    @Nullable Long getLong(ItemStack item, String key);

    /**
     * Gets a byte value from the item's NBT data.
     *
     * @param item the item to read from
     * @param key  the NBT key
     * @return the value, or {@code null} if absent or not a byte
     */
    @Nullable Byte getByte(ItemStack item, String key);

    /**
     * Gets a short value from the item's NBT data.
     *
     * @param item the item to read from
     * @param key  the NBT key
     * @return the value, or {@code null} if absent or not a short
     */
    @Nullable Short getShort(ItemStack item, String key);

    /**
     * Gets a boolean value from the item's NBT data.
     *
     * @param item the item to read from
     * @param key  the NBT key
     * @return the value, or {@code null} if absent or not a boolean
     */
    @Nullable Boolean getBoolean(ItemStack item, String key);

    /**
     * Gets an integer array from the item's NBT data.
     *
     * @param item the item to read from
     * @param key  the NBT key
     * @return the array, or {@code null} if absent or not an integer array
     */
    int[] getIntegerArray(ItemStack item, String key);

    /**
     * Gets a long array from the item's NBT data.
     *
     * @param item the item to read from
     * @param key  the NBT key
     * @return the array, or {@code null} if absent or not a long array
     */
    long[] getLongArray(ItemStack item, String key);

    /**
     * Gets a byte array from the item's NBT data.
     *
     * @param item the item to read from
     * @param key  the NBT key
     * @return the array, or {@code null} if absent or not a byte array
     */
    byte[] getByteArray(ItemStack item, String key);

    /**
     * Gets a value from the item's NBT data, returning a fallback if it is missing.
     *
     * @param item  the item to read from
     * @param key   the NBT key
     * @param value the fallback value
     * @param <T>   the value type
     * @return the stored value, or the fallback value
     */
    @Nullable <T> T getOrDefault(ItemStack item, String key, @Nullable T value);

    /**
     * Gets all entries from the item's NBT data.
     *
     * @param item the item to read from
     * @return a map of NBT keys to their values
     */
    Map<String, Object> getAll(ItemStack item);

    /**
     * Sets a string value in the item's NBT data.
     *
     * @param item  the item to write to
     * @param key   the NBT key
     * @param value the value to store
     */
    void set(ItemStack item, String key, @Nullable  String value);

    /**
     * Sets an integer value in the item's NBT data.
     *
     * @param item  the item to write to
     * @param key   the NBT key
     * @param value the value to store
     */
    void set(ItemStack item, String key, @Nullable  Integer value);

    /**
     * Sets a double value in the item's NBT data.
     *
     * @param item  the item to write to
     * @param key   the NBT key
     * @param value the value to store
     */
    void set(ItemStack item, String key, @Nullable  Double value);

    /**
     * Sets a float value in the item's NBT data.
     *
     * @param item  the item to write to
     * @param key   the NBT key
     * @param value the value to store
     */
    void set(ItemStack item, String key, @Nullable  Float value);

    /**
     * Sets a long value in the item's NBT data.
     *
     * @param item  the item to write to
     * @param key   the NBT key
     * @param value the value to store
     */
    void set(ItemStack item, String key, @Nullable  Long value);

    /**
     * Sets a short value in the item's NBT data.
     *
     * @param item  the item to write to
     * @param key   the NBT key
     * @param value the value to store
     */
    void set(ItemStack item, String key, @Nullable  Short value);

    /**
     * Sets a boolean value in the item's NBT data.
     *
     * @param item  the item to write to
     * @param key   the NBT key
     * @param value the value to store
     */
    void set(ItemStack item, String key, @Nullable  Boolean value);

    /**
     * Sets an integer array in the item's NBT data.
     *
     * @param item  the item to write to
     * @param key   the NBT key
     * @param value the array to store
     */
    void set(ItemStack item, String key, int[] value);

    /**
     * Sets a long array in the item's NBT data.
     *
     * @param item  the item to write to
     * @param key   the NBT key
     * @param value the array to store
     */
    void set(ItemStack item, String key, long[] value);

    /**
     * Sets a byte array in the item's NBT data.
     *
     * @param item  the item to write to
     * @param key   the NBT key
     * @param value the array to store
     */
    void set(ItemStack item, String key, byte[] value);

    /**
     * Removes a value from the item's NBT data.
     *
     * @param item the item to remove from
     * @param key  the NBT key
     */
    void remove(ItemStack item, String key);
}
