package io.github.tavstaldev.yggra.items.impl_1_20_4;

import io.github.tavstaldev.yggra.items.api.IRegistryAccessor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * {@inheritDoc}
 */
public class RegistryAccessor_1_20_4 implements IRegistryAccessor {
    /**
     * {@inheritDoc}
     */
    @Override
    public Enchantment getEnchantment(NamespacedKey key) {
        return Registry.ENCHANTMENT.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PotionType getPotionType(NamespacedKey key) {
        return Registry.POTION.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PotionEffectType getPotionEffectType(NamespacedKey key) {
        return Registry.POTION_EFFECT_TYPE.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityType getEntityType(NamespacedKey key) {
        return Registry.ENTITY_TYPE.get(key);
    }
}
