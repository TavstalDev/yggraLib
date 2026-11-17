package com.github.TavstalDev.yggraLib.impl.v1_14.items;

import com.github.TavstalDev.yggraLib.bukkit.items.IRegistryAccessor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**

 * Implementation of RegistryAccessor_1 for Minecraft 1.14.

 */
public class RegistryAccessor_1_14 implements IRegistryAccessor {
    /**
     * {@inheritDoc}
     */
    @Override
    public Enchantment getEnchantment(String key) {
        NamespacedKey namespacedKey = NamespacedKey.minecraft(key.toLowerCase());
        return Enchantment.getByKey(namespacedKey);
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
