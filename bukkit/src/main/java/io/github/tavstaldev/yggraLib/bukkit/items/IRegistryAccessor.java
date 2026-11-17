package io.github.tavstaldev.yggraLib.bukkit.items;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * Provides version-safe access to Bukkit registry values by key.
 */
public interface IRegistryAccessor {

    /**
     * Gets an enchantment by its key.
     *
     * @param key the enchantment key
     * @return the matching enchantment, or {@code null} if not found
     */
    Enchantment getEnchantment(String key);

    /**
     * Gets a potion type by its key.
     *
     * @param key the potion type key
     * @return the matching potion type, or {@code null} if not found
     */
    PotionType getPotionType(String key);

    /**
     * Gets a potion effect type by its key.
     *
     * @param key the potion effect type key
     * @return the matching potion effect type, or {@code null} if not found
     */
    PotionEffectType getPotionEffectType(String key);

    /**
     * Gets an entity type by its key.
     *
     * @param key the entity type key
     * @return the matching entity type, or {@code null} if not found
     */
    EntityType getEntityType(String key);
}
