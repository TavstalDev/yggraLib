package io.github.tavstaldev.yggraLib.impl.v1_21_5.items.meta;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.github.tavstaldev.yggraLib.bukkit.items.IMetaHandler;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

/**
 * A handler for serializing and deserializing the metadata of a {@link SkullMeta}.
 * This class provides static methods to convert the owner and player profile
 * of a skull item into a storable format and reconstruct them from the stored data.
 */
public class SkullMetaHandler_1_21_5 implements IMetaHandler {
    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof SkullMeta skullMeta))
            return;

        if (skullMeta.hasOwner() && skullMeta.getOwningPlayer() != null)
            itemData.put("owner", skullMeta.getOwningPlayer().getUniqueId());

        if (skullMeta.getPlayerProfile() != null) {
            var profile = skullMeta.getPlayerProfile();
            if (profile.hasTextures())
                itemData.put("profileUrl", profile.getTextures().getSkin());
            itemData.put("profile", profile.getId());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof SkullMeta skullMeta))
            return;

        if (itemData.containsKey("owner")) {
            var player = Bukkit.getOfflinePlayer((UUID) itemData.get("owner"));
            skullMeta.setOwningPlayer(player);
        }

        UUID profileUUID;
        // Restore player profile (for custom skins)
        if (itemData.containsKey("profile")) {
           profileUUID = (UUID) itemData.get("profile");
        }
        else {
            profileUUID = UUID.randomUUID();
        }

        var profile = Bukkit.createProfile(profileUUID);
        var textures = profile.getTextures();
        if (itemData.containsKey("profileUrl")) {
            textures.setSkin((URL) itemData.get("profileUrl"));
            profile.setTextures(textures);
        }
        else if (itemData.containsKey("headTexture")) {
            String texture = (String)itemData.get("headTexture");
            profile.setProperty(new ProfileProperty("textures", texture));
        }
        skullMeta.setPlayerProfile(profile);
    }
}