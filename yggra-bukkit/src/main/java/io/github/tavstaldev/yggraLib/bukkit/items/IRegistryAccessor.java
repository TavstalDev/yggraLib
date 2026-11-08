package io.github.tavstaldev.yggraLib.bukkit.items;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public interface IRegistryAccessor {

    Enchantment getEnchantment(String key);

    PotionType getPotionType(String key);

    PotionEffectType getPotionEffectType(String key);

    EntityType getEntityType(String key);
}
