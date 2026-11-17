package io.github.tavstaldev.yggraLib.impl.vmodern.items.meta;

import io.github.tavstaldev.yggraLib.bukkit.items.IMetaHandler;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

/**
 * Modern implementation of CrossbowMetaHandler for newer Minecraft versions.
 */
public class CrossbowMetaHandler_modern implements IMetaHandler {

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