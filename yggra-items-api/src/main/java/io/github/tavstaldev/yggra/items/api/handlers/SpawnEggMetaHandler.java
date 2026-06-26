package io.github.tavstaldev.yggra.items.api.handlers;

import io.github.tavstaldev.yggra.items.api.IMetaHandler;
import io.github.tavstaldev.yggra.items.api.ItemSerializerBase;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.Map;

/**
 * A handler for serializing and deserializing the custom entity type of {@link SpawnEggMeta}.
 * This class provides static methods to convert the custom entity type of spawn egg
 * into a storable format and reconstruct it from the stored data.
 */
public class SpawnEggMetaHandler implements IMetaHandler {
    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof SpawnEggMeta spawnEggMeta))
            return;

        if (spawnEggMeta.getSpawnedEntity() == null)
            return;

        if (spawnEggMeta.getCustomSpawnedType() == null)
            return;

        itemData.put("customEntityType", spawnEggMeta.getCustomSpawnedType().getKey().getKey());
    }

    /**
     * {@inheritDoc}
     */
    public void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof SpawnEggMeta spawnEggMeta))
            return;

        if (!itemData.containsKey("customEntityType")) {
            return;
        }

        String entityType = (String) itemData.get("customEntityType");
        var key = NamespacedKey.fromString(entityType);
        if (key != null)
            spawnEggMeta.setCustomSpawnedType(parent.registryAccessor.getEntityType(key));
    }
}