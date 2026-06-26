package io.github.tavstaldev.yggra.core.gui.renderer;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * A utility class that applies custom head textures to item metadata.
 */
public final class GuiHeadRenderer {

    /**
     * Applies a base64 texture string to a SkullMeta item.
     *
     * @param meta    The item meta to apply the texture to.
     * @param texture The base64 encoded texture string.
     */
    public static void apply(ItemMeta meta, String texture) {
        if (meta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", texture));
            skullMeta.setPlayerProfile(profile);
        }
    }
}
