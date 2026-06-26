package io.github.tavstaldev.yggra.items.api;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/**
 * Handles serialization and deserialization of {@link org.bukkit.inventory.meta.components.CustomModelDataComponent}
 * data (colors, flags, floats, strings) to/from an item data map.
 */
@SuppressWarnings("UnstableApiUsage")
public interface ICustomModelDataIO {

    /**
     * Serializes custom model data from the {@link ItemMeta} into the item data map.
     *
     * @param meta     the item meta to read from
     * @param itemData the output data map to write into
     */
    void serialize(ItemMeta meta, Map<String, Object> itemData);

    /**
     * Deserializes custom model data from the item data map into the {@link ItemMeta}.
     *
     * @param meta     the item meta to write to
     * @param itemData the input data map to read from
     */
    void deserialize(ItemMeta meta, Map<String, Object> itemData);
}
