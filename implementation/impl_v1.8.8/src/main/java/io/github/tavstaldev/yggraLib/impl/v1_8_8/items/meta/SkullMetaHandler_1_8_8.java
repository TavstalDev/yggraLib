package io.github.tavstaldev.yggraLib.impl.v1_8_8.items.meta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.github.tavstaldev.yggraLib.bukkit.items.IMetaHandler;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

/**
 * A handler for serializing and deserializing the metadata of a {@link SkullMeta}.
 * This class provides static methods to convert the owner and player profile
 * of a skull item into a storable format and reconstruct them from the stored data.
 */
@SuppressWarnings("CallToPrintStackTrace")
public class SkullMetaHandler_1_8_8 implements IMetaHandler {

    private static Field field;

    /**
     * {@inheritDoc}
     */
    public void serialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof SkullMeta))
            return;

        SkullMeta skullMeta = (SkullMeta)meta;
        if (skullMeta.hasOwner() && skullMeta.getOwner() != null)
            itemData.put("owner", skullMeta.getOwner());

        try {
            if (field == null) {
                field = skullMeta.getClass().getDeclaredField("profile");
            }
            field.setAccessible(true);
            GameProfile gameProfile = (GameProfile)field.get(skullMeta);
            if (gameProfile == null)
                return;

            PropertyMap propertyMap = gameProfile.getProperties();
            if (propertyMap.containsKey("textures")) {
                java.util.Collection<Property> textures = propertyMap.get("textures");
                for (Property property : textures) {
                    if (!Objects.equals(property.getName(), "textures"))
                        continue;

                    itemData.put("textures", property.getValue());
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deserialize(ItemSerializerBase parent, ItemMeta meta, Map<String, Object> itemData) throws Exception {
        if (!(meta instanceof SkullMeta))
            return;

        SkullMeta skullMeta = (SkullMeta)meta;
        if (itemData.containsKey("owner")) {
            skullMeta.setOwner((String)itemData.get("owner"));
        }

        if (itemData.containsKey("textures")) {
            String texture = (String)itemData.get("textures");
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            gameProfile.getProperties().put("textures", new Property("textures", texture));
            try {
                if (field == null) {
                    field = skullMeta.getClass().getDeclaredField("profile");
                }
                field.setAccessible(true);
                field.set(skullMeta, gameProfile);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}