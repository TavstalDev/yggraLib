package com.github.TavstalDev.yggraLib.bukkit.items;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface IPersistentDataMapper {

    /**
     * Reads all persistent data entries from the item and returns them as a
     * {@code Map<String, Object>} where each value is itself a map containing
     * the entry's type and value (e.g., {@code {"type": "STRING", "value": "foo"}}).
     *
     * @param item the item stack to read from
     * @return a map of namespaced-key-string to type-tagged value entries
     */
     Map<String, Object> map(ItemStack item);

    /**
     * Writes the given type-tagged entries back into the item's
     * PersistentDataContainer.
     * <p>
     * The map is expected to have the same format as produced by
     * {@link #map(ItemStack)}, i.e. each value is a map with
     * {@code "type"} and {@code "value"} keys.
     *
     * @param item the item stack to write to
     * @param map  the map of namespaced-key-string to type-tagged value entries
     */
    void set(ItemStack item, Map<String, Object> map);
}
