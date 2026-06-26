package io.github.tavstaldev.yggra.items.api;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/**
 * Handles serialization and deserialization of a specific type of item metadata
 * (e.g., enchantments, potion effects, book contents) to/from an item data map.
 *
 * @see EHandlerType
 */
public interface IMetaHandler {

    /**
     * Serializes the relevant metadata from the {@link ItemMeta} into the item data map.
     *
     * @param parent   the calling serializer instance, providing shared utilities
     * @param meta     the item meta to read from
     * @param itemData the output data map to write into
     */
    void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception;

    /**
     * Deserializes the relevant metadata from the item data map into the {@link ItemMeta}.
     *
     * @param parent   the calling serializer instance, providing shared utilities
     * @param meta     the item meta to write to
     * @param itemData the input data map to read from
     */
    void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception;
}
