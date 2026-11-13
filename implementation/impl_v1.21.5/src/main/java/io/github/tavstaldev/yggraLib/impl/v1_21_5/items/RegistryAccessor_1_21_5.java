package io.github.tavstaldev.yggraLib.impl.v1_21_5.items;

import io.github.tavstaldev.yggraLib.bukkit.items.IRegistryAccessor;
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
public class RegistryAccessor_1_21_5 implements IRegistryAccessor {
    /**
     * {@inheritDoc}
     */
    @Override
    public Enchantment getEnchantment(String key) {
        NamespacedKey namespacedKey;
        if (!key.contains(":"))
            namespacedKey = NamespacedKey.minecraft(key.toLowerCase());
        else
            namespacedKey = NamespacedKey.fromString(key);
        if (namespacedKey == null)
            return null;
        return RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT).get(namespacedKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PotionType getPotionType(String key) {
        NamespacedKey namespacedKey;
        if (!key.contains(":"))
            namespacedKey = NamespacedKey.minecraft(key.toLowerCase());
        else
            namespacedKey = NamespacedKey.fromString(key);
        if (namespacedKey == null)
            return null;
        return  RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.POTION).get(namespacedKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PotionEffectType getPotionEffectType(String key) {
        NamespacedKey namespacedKey;
        if (!key.contains(":"))
            namespacedKey = NamespacedKey.minecraft(key.toLowerCase());
        else
            namespacedKey = NamespacedKey.fromString(key);
        if (namespacedKey == null)
            return null;
        return RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.MOB_EFFECT).get(namespacedKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityType getEntityType(String key) {
        NamespacedKey namespacedKey;
        if (!key.contains(":"))
            namespacedKey = NamespacedKey.minecraft(key.toLowerCase());
        else
            namespacedKey = NamespacedKey.fromString(key);
        if (namespacedKey == null)
            return null;
        return  RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENTITY_TYPE).get(namespacedKey);
    }
}
