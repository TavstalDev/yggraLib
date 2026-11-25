package com.github.TavstalDev.yggraLib.impl.vmodern.items.meta;

import com.github.TavstalDev.yggraLib.bukkit.items.IItemSerializer;
import com.github.TavstalDev.yggraLib.bukkit.items.IMetaHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.Map;

/**
 * A handler for serializing and deserializing the custom entity type of {@link SpawnEggMeta}.
 * This class provides static methods to convert the custom entity type of spawn egg
 * into a storable format and reconstruct it from the stored data.
 */
public class SpawnEggMetaHandler_modern implements IMetaHandler {
    /** {@inheritDoc} */
    public void serialize(IItemSerializer parent, final ItemStack original, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof SpawnEggMeta spawnEggMeta))
            return;

        if (spawnEggMeta.getSpawnedEntity() == null)
            return;

        if (spawnEggMeta.getCustomSpawnedType() == null)
            return;

        itemData.put("customEntityType", spawnEggMeta.getCustomSpawnedType().getKey().getKey());
    }

    /** {@inheritDoc} */
    public void deserialize(IItemSerializer parent, ItemStack itemStack, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof SpawnEggMeta spawnEggMeta))
            return;

        if (!itemData.containsKey("customEntityType")) {
            return;
        }

        String entityType = (String) itemData.get("customEntityType");
        spawnEggMeta.setCustomSpawnedType(parent.registryAccessor().getEntityType(entityType));
    }
}