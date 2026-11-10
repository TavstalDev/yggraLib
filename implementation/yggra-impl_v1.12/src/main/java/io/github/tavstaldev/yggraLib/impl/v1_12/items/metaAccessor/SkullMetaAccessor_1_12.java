package io.github.tavstaldev.yggraLib.impl.v1_12.items.metaAccessor;

import io.github.tavstaldev.yggraLib.bukkit.items.metaAccessors.ISkullMetaAccessor;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullMetaAccessor_1_12 implements ISkullMetaAccessor {
    @Override
    public void setOwner(SkullMeta skullMeta, String owner) {
        skullMeta.setOwner(owner);
    }
}
