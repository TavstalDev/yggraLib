package io.github.tavstaldev.yggra.impl.v1_20_4.items;

import io.github.tavstaldev.yggra.bukkit.items.IPotionNameHandler;
import io.github.tavstaldev.yggra.bukkit.utils.AdventureUtils;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Objects;

/**
 * {@inheritDoc}
 */
public class PotionNameHandler_1_20_4 implements IPotionNameHandler {
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
        return PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(potionMeta.displayName()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomName(PotionMeta potionMeta, String name) {
        potionMeta.displayName(AdventureUtils.parse(name, true));
    }
}
