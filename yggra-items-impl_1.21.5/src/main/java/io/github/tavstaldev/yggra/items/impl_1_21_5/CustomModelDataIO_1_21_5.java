package io.github.tavstaldev.yggra.items.impl_1_21_5;

import io.github.tavstaldev.yggra.items.api.ICustomModelDataIO;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 */
public class CustomModelDataIO_1_21_5 implements ICustomModelDataIO {
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void serialize(ItemMeta meta, Map<String, Object> itemData) {
        if (meta.hasCustomModelDataComponent()) {
            var customModelData = meta.getCustomModelDataComponent();
            Map<String, List<?>> customModelDataMap = new HashMap<>();
            customModelDataMap.put("colors", customModelData.getColors());
            customModelDataMap.put("flags", customModelData.getFlags());
            customModelDataMap.put("floats", customModelData.getFloats());
            customModelDataMap.put("strings", customModelData.getStrings());
            itemData.put("customModelDataComponent", customModelDataMap);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void deserialize(ItemMeta meta, Map<String, Object> itemData) {
        if (itemData.containsKey("customModelDataComponent"))
        {
            var customModelData = itemData.get("customModelDataComponent");
            if (customModelData instanceof Map<?, ?> customModelDataMap) {
                List<Color> colors = (List<Color>) customModelDataMap.get("colors");
                List<Boolean> flags = (List<Boolean>) customModelDataMap.get("flags");
                List<Float> floats = (List<Float>) customModelDataMap.get("floats");
                List<String> strings = (List<String>) customModelDataMap.get("strings");

                CustomModelDataComponent component = meta.getCustomModelDataComponent();
                component.setColors(colors);
                component.setFlags(flags);
                component.setFloats(floats);
                component.setStrings(strings);
                meta.setCustomModelDataComponent(component);
            }
        }
    }
}
