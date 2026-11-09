package io.github.tavstaldev.yggraLib.impl.v1_21_5.items;

import io.github.tavstaldev.yggraLib.bukkit.items.ICustomModelDataIO;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 */
@SuppressWarnings("UnstableApiUsage")
public class CustomModelDataIO_1_21_5 implements ICustomModelDataIO {
    /**
     * {@inheritDoc}
     */
    @Override
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
    public void deserialize(ItemMeta meta, Map<String, Object> itemData) {
        if (itemData.containsKey("customModelDataComponent"))
        {
            var customModelData = itemData.get("customModelDataComponent");
            if (customModelData instanceof Map<?, ?> customModelDataMap) {
                List<Color> colors = new ArrayList<>();
                if (customModelDataMap.containsKey("colors")) {
                    //noinspection unchecked
                    colors = (List<Color>) customModelDataMap.get("colors");
                }
                List<Boolean> flags = new ArrayList<>();
                if (customModelDataMap.containsKey("flags")) {
                    //noinspection unchecked
                    flags = (List<Boolean>) customModelDataMap.get("flags");
                }
                List<Float> floats = new ArrayList<>();
                if (customModelDataMap.containsKey("floats")) {
                    List<?> rawList = (List<?>) customModelDataMap.get("floats");
                    for (Object obj : rawList) {
                        if (obj instanceof Number number) {
                            floats.add(number.floatValue());
                        }
                    }
                }
                List<String> strings = new ArrayList<>();
                if (customModelDataMap.containsKey("strings")) {
                    //noinspection unchecked
                    strings = (List<String>) customModelDataMap.get("strings");
                }

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
