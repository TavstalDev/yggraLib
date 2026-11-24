package com.github.TavstalDev.yggraLib.impl.v1_14.items.metaAccessor;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.TavstalDev.yggraLib.bukkit.items.metaAccessors.ISkullMetaAccessor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * Implementation of SkullMetaAccessor for Minecraft 1.14.
 */
public class SkullMetaAccessor_1_14 implements ISkullMetaAccessor {
    /** {@inheritDoc} */
    @Override
    public void setTexture(ItemStack item, String texture) {
        SkullMeta skullMeta = (SkullMeta)item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", texture));
        skullMeta.setPlayerProfile(profile);
        item.setItemMeta(skullMeta);
    }
}
