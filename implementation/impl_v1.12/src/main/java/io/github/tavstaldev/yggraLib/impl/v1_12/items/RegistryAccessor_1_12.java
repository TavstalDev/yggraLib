package io.github.tavstaldev.yggraLib.impl.v1_12.items;

import io.github.tavstaldev.yggraLib.bukkit.items.IRegistryAccessor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**

 * Implementation of RegistryAccessor_1 for Minecraft 1.12.

 */
public class RegistryAccessor_1_12 implements IRegistryAccessor {
    /**
     * {@inheritDoc}
     */
    @Override
    public Enchantment getEnchantment(String key) {
        return Enchantment.getByName(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PotionType getPotionType(String key) {
        return PotionType.valueOf(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PotionEffectType getPotionEffectType(String key) {
        return PotionEffectType.getByName(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityType getEntityType(String key) {
        return EntityType.valueOf(key);
    }
}
