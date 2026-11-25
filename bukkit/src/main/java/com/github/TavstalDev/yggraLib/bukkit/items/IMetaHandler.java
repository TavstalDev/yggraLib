package com.github.TavstalDev.yggraLib.bukkit.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/**
 * Handles serialization and deserialization for a specific type of item metadata.
 */
public interface IMetaHandler {

    /**
     * Writes supported metadata from an item into a data map.
     *
     * @param parent   the parent serializer
     * @param original the item to read from
     * @param meta     the item metadata to read from
     * @param itemData the target map for serialized data
     * @throws Exception if serialization fails
     */
    void serialize(IItemSerializer parent, final ItemStack original, ItemMeta meta, Map<String, Object> itemData) throws Exception;

    /**
     * Applies supported metadata from a data map to an item.
     *
     * @param parent   the parent serializer
     * @param meta     the item metadata to update
     * @param itemData the source map with serialized data
     * @throws Exception if deserialization fails
     */
    void deserialize(IItemSerializer parent, ItemStack itemStack, ItemMeta meta, Map<String, Object> itemData) throws Exception;
}
