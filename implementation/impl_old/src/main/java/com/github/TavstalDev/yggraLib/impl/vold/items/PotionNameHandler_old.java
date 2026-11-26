package com.github.TavstalDev.yggraLib.impl.vold.items;

import com.github.TavstalDev.yggraLib.bukkit.items.IPotionNameHandler;
import org.bukkit.inventory.meta.PotionMeta;

/**

 * Legacy implementation of PotionNameHandler for older Minecraft versions.

 */
public class PotionNameHandler_old implements IPotionNameHandler {
    /** {@inheritDoc} */
    @Override
    public boolean hasCustomName(PotionMeta potionMeta) {
        return potionMeta.hasDisplayName();
    }

    /** {@inheritDoc} */
    @Override
    public String getCustomName(PotionMeta potionMeta) {
        return potionMeta.getDisplayName();
    }

    /** {@inheritDoc} */
    @Override
    public void setCustomName(PotionMeta potionMeta, String name) {
        potionMeta.setDisplayName(name);
    }
}
