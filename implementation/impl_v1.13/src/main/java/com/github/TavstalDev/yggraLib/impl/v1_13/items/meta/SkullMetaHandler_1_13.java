package com.github.TavstalDev.yggraLib.impl.v1_13.items.meta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.TavstalDev.yggraLib.bukkit.items.IItemSerializer;
import com.github.TavstalDev.yggraLib.bukkit.items.IMetaHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * A handler for serializing and deserializing the metadata of a {@link SkullMeta}.
 * This class provides static methods to convert the owner and player profile
 * of a skull item into a storable format and reconstruct them from the stored data.
 */
public class SkullMetaHandler_1_13 implements IMetaHandler {
    /** {@inheritDoc} */
    public void serialize(IItemSerializer parent, final ItemStack original, ItemMeta meta, Map<String, Object> itemData) {
        if (!(meta instanceof SkullMeta))
            return;

        SkullMeta skullMeta = (SkullMeta)meta;
        if (skullMeta.hasOwner() && skullMeta.getOwningPlayer() != null)
            itemData.put("owner", skullMeta.getOwningPlayer().getUniqueId());

        PlayerProfile profile = skullMeta.getPlayerProfile();
        if (profile != null) {
            for (ProfileProperty prop : profile.getProperties()) {
                if (!prop.getName().equals("textures")) continue;
                try {
                    String json = new String(Base64.getDecoder().decode(prop.getValue()), StandardCharsets.UTF_8);
                    JsonObject skin = new JsonParser().parse(json)
                            .getAsJsonObject()
                            .getAsJsonObject("textures")
                            .getAsJsonObject("SKIN");
                    itemData.put("profileUrl", skin.get("url").getAsString());
                } catch (Exception ignored) {
                    // malformed or missing skin data
                }
                break;
            }
            if (profile.getId() != null) {
                itemData.put("profile", profile.getId().toString());
            }
        }
    }

    /** {@inheritDoc} */
    public void deserialize(IItemSerializer parent, ItemMeta meta, Map<String, Object> itemData) {
        if (!(meta instanceof SkullMeta))
            return;

        SkullMeta skullMeta = (SkullMeta)meta;

        if (itemData.containsKey("owner")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer((UUID) itemData.get("owner"));
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

        PlayerProfile profile = Bukkit.createProfile(profileUUID);
        if (itemData.containsKey("headTexture")) {
            String texture = (String)itemData.get("headTexture");
            profile.setProperty(new ProfileProperty("textures", texture));
        }
        skullMeta.setPlayerProfile(profile);
    }
}