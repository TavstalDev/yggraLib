package com.github.TavstalDev.yggraLib.impl.v1_20_4.items;

import com.github.TavstalDev.yggraLib.bukkit.items.IRegistryAccessor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**

 * Implementation of RegistryAccessor_1_20 for Minecraft 1.20.4.

 */
public class RegistryAccessor_1_20_4 implements IRegistryAccessor {
    /** {@inheritDoc} */
    @Override
    public Enchantment getEnchantment(String key) {
        NamespacedKey namespacedKey;
        if (!key.contains(":"))
            namespacedKey = NamespacedKey.minecraft(key.toLowerCase());
        else
            namespacedKey = NamespacedKey.fromString(key);
        if (namespacedKey == null)
            return null;
        return Registry.ENCHANTMENT.get(namespacedKey);
    }

    /** {@inheritDoc} */
    @Override
    public PotionType getPotionType(String key) {
        NamespacedKey namespacedKey;
        if (!key.contains(":"))
            namespacedKey = NamespacedKey.minecraft(key.toLowerCase());
        else
            namespacedKey = NamespacedKey.fromString(key);
        if (namespacedKey == null)
            return null;
        return Registry.POTION.get(namespacedKey);
    }

    /** {@inheritDoc} */
    @Override
    public PotionEffectType getPotionEffectType(String key) {
        NamespacedKey namespacedKey;
        if (!key.contains(":"))
            namespacedKey = NamespacedKey.minecraft(key.toLowerCase());
        else
            namespacedKey = NamespacedKey.fromString(key);
        if (namespacedKey == null)
            return null;
        return Registry.POTION_EFFECT_TYPE.get(namespacedKey);
    }

    /** {@inheritDoc} */
    @Override
    public EntityType getEntityType(String key) {
        NamespacedKey namespacedKey;
        if (!key.contains(":"))
            namespacedKey = NamespacedKey.minecraft(key.toLowerCase());
        else
            namespacedKey = NamespacedKey.fromString(key);
        if (namespacedKey == null)
            return null;
        return Registry.ENTITY_TYPE.get(namespacedKey);
    }
}
