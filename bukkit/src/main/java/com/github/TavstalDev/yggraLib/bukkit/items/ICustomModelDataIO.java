package com.github.TavstalDev.yggraLib.bukkit.items;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/**
 * Handles reading and writing custom model data for different server versions.
 */
public interface ICustomModelDataIO {

    /**
     * Writes custom model data from item metadata into a data map.
     *
     * @param meta     the item metadata to read from
     * @param itemData the target map for serialized data
     */
    void serialize(ItemMeta meta, Map<String, Object> itemData);

    /**
     * Applies custom model data from a data map to item metadata.
     *
     * @param meta     the item metadata to update
     * @param itemData the source map with serialized data
     */
    void deserialize(ItemMeta meta, Map<String, Object> itemData);
}
