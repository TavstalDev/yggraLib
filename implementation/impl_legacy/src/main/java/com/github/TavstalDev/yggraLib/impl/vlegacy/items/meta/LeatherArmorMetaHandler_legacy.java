package com.github.TavstalDev.yggraLib.impl.vlegacy.items.meta;

import com.github.TavstalDev.yggraLib.bukkit.items.IItemSerializer;
import com.github.TavstalDev.yggraLib.bukkit.items.IMetaHandler;
import com.github.TavstalDev.yggraLib.impl.vlegacy.utils.ColorUtils_legacy;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Map;

/**
 * A handler for serializing and deserializing the color metadata of a {@link LeatherArmorMeta}.
 * This class provides static methods to convert the color of leather armor into a storable format
 * and reconstruct it from the stored data.
 */
public class LeatherArmorMetaHandler_legacy implements IMetaHandler {
    /** {@inheritDoc} */
    public void serialize(IItemSerializer parent, final ItemStack original, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof LeatherArmorMeta))
            return;

        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)meta;
        Color color = leatherArmorMeta.getColor();
        itemData.put("color", ColorUtils_legacy.serializeColor(color));
    }

    /** {@inheritDoc} */
    public void deserialize(IItemSerializer parent, ItemStack itemStack, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof LeatherArmorMeta))
            return;

        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)meta;
        if (!itemData.containsKey("color"))
            return;

        String colorData = (String) itemData.get("color");
        leatherArmorMeta.setColor(ColorUtils_legacy.deserializeColor(colorData));
    }
}