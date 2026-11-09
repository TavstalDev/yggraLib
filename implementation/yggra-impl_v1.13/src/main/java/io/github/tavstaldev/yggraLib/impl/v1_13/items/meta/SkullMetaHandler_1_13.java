package io.github.tavstaldev.yggraLib.impl.v1_13.items.meta;

import io.github.tavstaldev.yggraLib.bukkit.items.IMetaHandler;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;

/**
 * A handler for serializing and deserializing the metadata of a {@link SkullMeta}.
 * This class provides static methods to convert the owner and player profile
 * of a skull item into a storable format and reconstruct them from the stored data.
 */
public class SkullMetaHandler_1_13 implements IMetaHandler {
    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof SkullMeta))
            return;

        SkullMeta skullMeta = (SkullMeta)meta;
        if (skullMeta.hasOwner() && skullMeta.getOwner() != null)
            itemData.put("owner", skullMeta.getOwner());
    }

    /**
     * {@inheritDoc}
     */
    public void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof SkullMeta))
            return;

        SkullMeta skullMeta = (SkullMeta)meta;
        if (itemData.containsKey("owner")) {
            skullMeta.setOwner((String)itemData.get("owner"));
        }
    }
}