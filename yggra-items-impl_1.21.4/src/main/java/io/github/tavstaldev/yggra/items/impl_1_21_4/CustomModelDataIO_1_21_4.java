package io.github.tavstaldev.yggra.items.impl_1_21_4;

import io.github.tavstaldev.yggra.items.api.ICustomModelDataIO;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/**
 * {@inheritDoc}
 */
public class CustomModelDataIO_1_21_4 implements ICustomModelDataIO {
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
