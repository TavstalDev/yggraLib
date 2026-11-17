package io.github.tavstaldev.yggraLib.impl.vmodern.items.metaAccessor;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.github.tavstaldev.yggraLib.bukkit.items.metaAccessors.ISkullMetaAccessor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * Modern implementation of SkullMetaAccessor for newer Minecraft versions.
 */
public class SkullMetaAccessor_modern implements ISkullMetaAccessor {
    /**
     * Sets the value for setTexture.
     * @param item the item value
     * @param texture the texture value
     */
    @Override
    public void setTexture(ItemStack item, String texture) {
        SkullMeta skullMeta = (SkullMeta)item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
        profile.setProperty(new ProfileProperty("textures", texture));
        skullMeta.setPlayerProfile(profile);
        item.setItemMeta(skullMeta);
    }
}
