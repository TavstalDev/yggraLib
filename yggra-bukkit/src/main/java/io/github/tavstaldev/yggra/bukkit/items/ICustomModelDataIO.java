package io.github.tavstaldev.yggra.bukkit.items;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public interface ICustomModelDataIO {

    void serialize(ItemMeta meta, Map<String, Object> itemData);

    void deserialize(ItemMeta meta, Map<String, Object> itemData);
}
