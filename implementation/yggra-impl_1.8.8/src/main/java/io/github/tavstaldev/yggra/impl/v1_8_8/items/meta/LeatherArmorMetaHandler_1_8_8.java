package io.github.tavstaldev.yggra.impl.v1_8_8.items.meta;

import io.github.tavstaldev.yggra.bukkit.items.IMetaHandler;
import io.github.tavstaldev.yggra.bukkit.items.ItemSerializerBase;
import io.github.tavstaldev.yggra.impl.v1_8_8.utils.ColorUtils_1_8_8;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Map;

/**
 * A handler for serializing and deserializing the color metadata of a {@link LeatherArmorMeta}.
 * This class provides static methods to convert the color of leather armor into a storable format
 * and reconstruct it from the stored data.
 */
public class LeatherArmorMetaHandler_1_8_8 implements IMetaHandler {
    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof LeatherArmorMeta))
            return;

        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)meta;
        Color color = leatherArmorMeta.getColor();
        itemData.put("color", ColorUtils_1_8_8.serializeColor(color));
    }

    /**
     * {@inheritDoc}
     */
    public void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof LeatherArmorMeta))
            return;

        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)meta;
        if (!itemData.containsKey("color"))
            return;

        String colorData = (String) itemData.get("color");
        leatherArmorMeta.setColor(ColorUtils_1_8_8.deserializeColor(colorData));
    }
}