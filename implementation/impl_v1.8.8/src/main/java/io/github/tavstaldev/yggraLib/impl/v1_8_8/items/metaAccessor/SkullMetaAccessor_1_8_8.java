package io.github.tavstaldev.yggraLib.impl.v1_8_8.items.metaAccessor;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.tavstaldev.yggraLib.bukkit.items.metaAccessors.ISkullMetaAccessor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

@SuppressWarnings("CallToPrintStackTrace")
public class SkullMetaAccessor_1_8_8 implements ISkullMetaAccessor {
    private static Field field;

    @Override
    public void setTexture(ItemStack item, String texture) {
        SkullMeta skullMeta = (SkullMeta)item.getItemMeta();
        if (texture.length() < 16) {
            skullMeta.setOwner(texture);
            item.setItemMeta(skullMeta);
            return;
        }
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
        item.setItemMeta(skullMeta);
    }
}
