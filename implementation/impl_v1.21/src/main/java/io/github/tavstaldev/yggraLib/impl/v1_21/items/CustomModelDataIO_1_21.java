package io.github.tavstaldev.yggraLib.impl.v1_21.items;

import io.github.tavstaldev.yggraLib.bukkit.items.ICustomModelDataIO;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/**

 * Implementation of CustomModelDataIO_1 for Minecraft 1.21.

 */
public class CustomModelDataIO_1_21 implements ICustomModelDataIO {
    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(ItemMeta meta, Map<String, Object> itemData) {
        if (meta.hasCustomModelData())
            itemData.put("customModelData", meta.getCustomModelData());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deserialize(ItemMeta meta, Map<String, Object> itemData) {
        if (itemData.containsKey("customModelData"))
            meta.setCustomModelData((int)itemData.get("customModelData"));
    }
}
