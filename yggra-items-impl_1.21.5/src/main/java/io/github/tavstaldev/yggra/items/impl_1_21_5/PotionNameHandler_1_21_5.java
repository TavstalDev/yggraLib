package io.github.tavstaldev.yggra.items.impl_1_21_5;

import io.github.tavstaldev.yggra.items.api.IPotionNameHandler;
import org.bukkit.inventory.meta.PotionMeta;

/**
 * {@inheritDoc}
 */
public class PotionNameHandler_1_21_5 implements IPotionNameHandler {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCustomName(PotionMeta potionMeta) {
        return potionMeta.hasCustomPotionName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustomName(PotionMeta potionMeta) {
        return potionMeta.getCustomPotionName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomName(PotionMeta potionMeta, String name) {
        potionMeta.setCustomPotionName(name);
    }
}
