package io.github.tavstaldev.yggraLib.impl.vmodern.items.metaAccessor;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.github.tavstaldev.yggraLib.bukkit.items.metaAccessors.ISkullMetaAccessor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class SkullMetaAccessor_modern implements ISkullMetaAccessor {
    @Override
    public void setOwner(SkullMeta skullMeta, String owner) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", owner));
        skullMeta.setPlayerProfile(profile);
    }
}
