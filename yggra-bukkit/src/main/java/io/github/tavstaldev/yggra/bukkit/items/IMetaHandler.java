package io.github.tavstaldev.yggra.bukkit.items;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public interface IMetaHandler {

    void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception;

    void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception;
}
