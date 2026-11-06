package io.github.tavstaldev.yggra.bukkit.items;

import org.bukkit.inventory.meta.PotionMeta;

/**
 * Handles version-specific access to custom potion names.
 * <p>
 * The method used to store/retrieve custom potion names differs across Minecraft versions
 * (e.g., {@code PotionMeta.hasCustomName()} was added in later versions).
 * This interface abstracts away those differences.
 */
public interface IPotionNameHandler {

    /**
     * Checks whether the given {@link PotionMeta} has a custom name.
     *
     * @param potionMeta the potion meta to check
     * @return {@code true} if a custom name is present
     */
    boolean hasCustomName(PotionMeta potionMeta);

    /**
     * Returns the custom name of the given {@link PotionMeta}.
     *
     * @param potionMeta the potion meta to read from
     * @return the custom name, or {@code null} if none is set
     */
    String getCustomName(PotionMeta potionMeta);

    /**
     * Sets the custom name on the given {@link PotionMeta}.
     *
     * @param potionMeta the potion meta to write to
     * @param name       the custom name to set
     */
    void setCustomName(PotionMeta potionMeta, String name);
}
