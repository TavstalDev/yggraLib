package io.github.tavstaldev.yggraLib.impl.vmodern.items;

import io.github.tavstaldev.yggraLib.bukkit.items.IPotionNameHandler;
import io.github.tavstaldev.yggraLib.bukkit.utils.AdventureUtils;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Objects;

/**
 * {@inheritDoc}
 */
public class PotionNameHandler_modern implements IPotionNameHandler {
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
