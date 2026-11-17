package com.github.TavstalDev.yggraLib.impl.vmodern.items;

import com.github.TavstalDev.yggraLib.bukkit.items.IPotionNameHandler;
import com.github.TavstalDev.yggraLib.bukkit.utils.AdventureUtils;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Objects;

/**

 * Modern implementation of PotionNameHandler for newer Minecraft versions.

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
