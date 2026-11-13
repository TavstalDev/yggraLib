package io.github.tavstaldev.yggraLib.impl.v1_8_8.items.metaAccessor;

import io.github.tavstaldev.yggraLib.bukkit.items.metaAccessors.ISkullMetaAccessor;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullMetaAccessor_1_8_8 implements ISkullMetaAccessor {
    @Override
    public void setOwner(SkullMeta skullMeta, String owner) {
        skullMeta.setOwner(owner);
    }
}
