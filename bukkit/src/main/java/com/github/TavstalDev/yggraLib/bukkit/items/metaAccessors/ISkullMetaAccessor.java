package com.github.TavstalDev.yggraLib.bukkit.items.metaAccessors;

import org.bukkit.inventory.ItemStack;

/**
 * Provides version-safe access to skull texture data.
 */
public interface ISkullMetaAccessor {

    /**
     * Applies a texture value to a skull item.
     *
     * @param item    the skull item to update
     * @param texture the texture value to apply
     */
    void setTexture(ItemStack item, String texture);
}
