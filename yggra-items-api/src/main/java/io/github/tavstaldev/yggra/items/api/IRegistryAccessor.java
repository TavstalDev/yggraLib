package io.github.tavstaldev.yggra.items.api;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * Provides version-independent access to Bukkit registries.
 * <p>
 * Registry lookup methods changed across Minecraft versions (e.g.,
 * {@code Registry.ENCHANTMENT} in 1.21+ vs. {@code Enchantment.getByKey()} in earlier versions).
 * This interface abstracts those differences.
 */
public interface IRegistryAccessor {

    /**
     * Looks up an {@link Enchantment} by its {@link NamespacedKey}.
     *
     * @param key the namespaced key of the enchantment
     * @return the enchantment, or {@code null} if not found
     */
    Enchantment getEnchantment(NamespacedKey key);

    /**
     * Looks up a {@link PotionType} by its {@link NamespacedKey}.
     *
     * @param key the namespaced key of the potion type
     * @return the potion type, or {@code null} if not found
     */
    PotionType getPotionType(NamespacedKey key);

    /**
     * Looks up a {@link PotionEffectType} by its {@link NamespacedKey}.
     *
     * @param key the namespaced key of the potion effect type
     * @return the potion effect type, or {@code null} if not found
     */
    PotionEffectType getPotionEffectType(NamespacedKey key);

    /**
     * Looks up an {@link EntityType} by its {@link NamespacedKey}.
     *
     * @param key the namespaced key of the entity type
     * @return the entity type, or {@code null} if not found
     */
    EntityType getEntityType(NamespacedKey key);
}
