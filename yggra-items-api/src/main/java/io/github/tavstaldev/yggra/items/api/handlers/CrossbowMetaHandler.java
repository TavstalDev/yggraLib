package io.github.tavstaldev.yggra.items.api.handlers;

import io.github.tavstaldev.yggra.items.api.IMetaHandler;
import io.github.tavstaldev.yggra.items.api.ItemSerializerBase;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

/**
 * A handler for serializing and deserializing the charged projectiles of a {@link CrossbowMeta}.
 * This class provides static methods to convert the charged projectiles of a crossbow
 * into a storable format and reconstruct them from the stored data.
 */
public class CrossbowMetaHandler implements IMetaHandler {

    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof CrossbowMeta crossbowMeta))
            return;

        if (!crossbowMeta.hasChargedProjectiles())
            return;

        List<ItemStack> projectiles = crossbowMeta.getChargedProjectiles();
        var projectileData = parent.serializeItemStackListToBytes(projectiles);
        itemData.put("projectiles", projectileData);
    }

    /**
     * {@inheritDoc}
     */
    public void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof CrossbowMeta crossbowMeta))
            return;

        if (!itemData.containsKey("projectiles"))
            return;

        var rawProjectiles = itemData.get("projectiles");
        if (!(rawProjectiles instanceof byte[])) {
            throw new Exception("Expected projectiles data to be a byte array.");
        }

        List<ItemStack> projectiles = parent.deserializeItemStackListFromBytes((byte[])rawProjectiles);
        crossbowMeta.setChargedProjectiles(projectiles);
    }
}