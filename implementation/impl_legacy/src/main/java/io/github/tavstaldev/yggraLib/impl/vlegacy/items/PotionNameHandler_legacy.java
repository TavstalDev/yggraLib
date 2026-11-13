package io.github.tavstaldev.yggraLib.impl.vlegacy.items;

import io.github.tavstaldev.yggraLib.bukkit.items.IPotionNameHandler;
import org.bukkit.inventory.meta.PotionMeta;

/**
 * {@inheritDoc}
 */
public class PotionNameHandler_legacy implements IPotionNameHandler {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCustomName(PotionMeta potionMeta) {
        return potionMeta.hasDisplayName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustomName(PotionMeta potionMeta) {
        return potionMeta.getDisplayName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomName(PotionMeta potionMeta, String name) {
        potionMeta.setDisplayName(name);
    }
}
