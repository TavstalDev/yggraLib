package io.github.tavstaldev.yggra.impl.v1_8_8.items;

import io.github.tavstaldev.yggra.bukkit.items.IPotionNameHandler;
import org.bukkit.inventory.meta.PotionMeta;

/**
 * {@inheritDoc}
 */
public class PotionNameHandler_1_8_8 implements IPotionNameHandler {
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
