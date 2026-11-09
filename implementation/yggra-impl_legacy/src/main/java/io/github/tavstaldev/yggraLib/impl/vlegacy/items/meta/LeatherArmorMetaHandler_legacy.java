package io.github.tavstaldev.yggraLib.impl.vlegacy.items.meta;

import io.github.tavstaldev.yggraLib.bukkit.items.IMetaHandler;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import io.github.tavstaldev.yggraLib.impl.vlegacy.utils.ColorUtils_legacy;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Map;

/**
 * A handler for serializing and deserializing the color metadata of a {@link LeatherArmorMeta}.
 * This class provides static methods to convert the color of leather armor into a storable format
 * and reconstruct it from the stored data.
 */
public class LeatherArmorMetaHandler_legacy implements IMetaHandler {
    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof LeatherArmorMeta))
            return;

        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)meta;
        Color color = leatherArmorMeta.getColor();
        itemData.put("color", ColorUtils_legacy.serializeColor(color));
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
        leatherArmorMeta.setColor(ColorUtils_legacy.deserializeColor(colorData));
    }
}