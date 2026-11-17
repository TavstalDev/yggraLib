package com.github.TavstalDev.yggraLib.impl.v1_20_4.items;

import com.github.TavstalDev.yggraLib.bukkit.items.ICustomModelDataIO;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/**

 * Implementation of CustomModelDataIO_1_20 for Minecraft 1.20.4.

 */
public class CustomModelDataIO_1_20_4 implements ICustomModelDataIO {
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
