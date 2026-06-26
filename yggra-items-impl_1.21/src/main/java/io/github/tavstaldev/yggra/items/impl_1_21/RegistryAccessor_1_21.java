package io.github.tavstaldev.yggra.items.impl_1_21;

import io.github.tavstaldev.yggra.items.api.IRegistryAccessor;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * {@inheritDoc}
 */
public class RegistryAccessor_1_21 implements IRegistryAccessor {
    /**
     * {@inheritDoc}
     */
    @Override
    public Enchantment getEnchantment(NamespacedKey key) {

        return RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT).get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PotionType getPotionType(NamespacedKey key) {

        return  RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.POTION).get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PotionEffectType getPotionEffectType(NamespacedKey key) {

        return RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.MOB_EFFECT).get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityType getEntityType(NamespacedKey key) {

        return  RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENTITY_TYPE).get(key);
    }
}
