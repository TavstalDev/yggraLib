package com.github.TavstalDev.yggraLib.impl.v1_14.items.meta;

import com.github.TavstalDev.yggraLib.bukkit.items.IItemSerializer;
import com.github.TavstalDev.yggraLib.bukkit.items.IMetaHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

/**
 * Implementation of CrossbowMetaHandler for Minecraft 1.14.
 */
public class CrossbowMetaHandler_1_14 implements IMetaHandler {

    /** {@inheritDoc} */
    public void serialize(IItemSerializer parent, ItemMeta meta, Map<String, Object> itemData) {
        if (!(meta instanceof CrossbowMeta))
            return;

        CrossbowMeta crossbowMeta = (CrossbowMeta)meta;
        if (!crossbowMeta.hasChargedProjectiles())
            return;

        List<ItemStack> projectiles = crossbowMeta.getChargedProjectiles();
        byte[] projectileData = parent.serializeItemStackListToBytes(projectiles);
        itemData.put("projectiles", projectileData);
    }

    /** {@inheritDoc} */
    public void deserialize(IItemSerializer parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof CrossbowMeta))
            return;

        CrossbowMeta crossbowMeta = (CrossbowMeta)meta;
        if (!itemData.containsKey("projectiles"))
            return;

        Object rawProjectiles = itemData.get("projectiles");
        if (!(rawProjectiles instanceof byte[])) {
            throw new Exception("Expected projectiles data to be a byte array.");
        }

        List<ItemStack> projectiles = parent.deserializeItemStackListFromBytes((byte[])rawProjectiles);
        crossbowMeta.setChargedProjectiles(projectiles);
    }
}