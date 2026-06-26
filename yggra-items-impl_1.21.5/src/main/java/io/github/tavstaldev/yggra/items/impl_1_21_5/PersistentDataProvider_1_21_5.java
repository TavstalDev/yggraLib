package io.github.tavstaldev.yggra.items.impl_1_21_5;

import io.github.tavstaldev.yggra.items.api.PersistentDataProviderBase;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;

/**
 * {@inheritDoc}
 */
public class PersistentDataProvider_1_21_5 extends PersistentDataProviderBase {
    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable PersistentDataContainer getContainer(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer();
    }
}
