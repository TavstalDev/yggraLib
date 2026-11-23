package com.github.TavstalDev.yggraLib.bukkit.utils;

import com.github.TavstalDev.yggraLib.bukkit.items.INbtAccessor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Utility methods for writing NBT values to {@link ItemStack}s through an {@link INbtAccessor}.
 * <p>
 * This class dispatches a generic value to the appropriate type-specific setter
 * of the accessor, avoiding the need to cast values at call sites.
 */
public class NbtUtils {

    /**
     * Writes a value to an item's NBT data using the given accessor.
     * <p>
     * Supported value types are {@link String}, {@link Integer}, {@link Double},
     * {@link Float}, {@link Long}, {@link Short}, {@link Boolean}, {@code int[]},
     * {@code long[]}, and {@code byte[]}. A {@code null} value removes the key
     * from the item's NBT data. Calls with a null accessor, item, or key are ignored.
     *
     * @param nbtAccessor the accessor used to write the NBT data
     * @param itemStack   the item to write to
     * @param key         the NBT key
     * @param value       the value to store, or {@code null} to remove the key
     * @throws IllegalArgumentException if the value type is not supported
     */
    public static void set(INbtAccessor nbtAccessor, ItemStack itemStack, String key, @Nullable Object value) {
        if (nbtAccessor == null || itemStack == null || key == null) {
            return;
        }

        if (value == null) {
            nbtAccessor.remove(itemStack, key);
            return;
        }

        if (value instanceof String) {
            nbtAccessor.set(itemStack, key, (String) value);
        } else if (value instanceof Integer) {
            nbtAccessor.set(itemStack, key, (Integer) value);
        } else if (value instanceof Double) {
            nbtAccessor.set(itemStack, key, (Double) value);
        } else if (value instanceof Float) {
            nbtAccessor.set(itemStack, key, (Float) value);
        } else if (value instanceof Long) {
            nbtAccessor.set(itemStack, key, (Long) value);
        } else if (value instanceof Short) {
            nbtAccessor.set(itemStack, key, (Short) value);
        } else if (value instanceof Boolean) {
            nbtAccessor.set(itemStack, key, (Boolean) value);
        } else if (value instanceof int[]) {
            nbtAccessor.set(itemStack, key, (int[]) value);
        } else if (value instanceof long[]) {
            nbtAccessor.set(itemStack, key, (long[]) value);
        } else if (value instanceof byte[]) {
            nbtAccessor.set(itemStack, key, (byte[]) value);
        } else {
            throw new IllegalArgumentException("Unsupported NBT value type: " + value.getClass().getName());
        }
    }
}
