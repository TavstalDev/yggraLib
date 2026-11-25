package com.github.TavstalDev.yggraLib.impl.v1_8_8.items;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XPotion;
import com.github.TavstalDev.yggraLib.bukkit.items.IRegistryAccessor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Optional;

/**

 * Implementation of RegistryAccessor_1_8 for Minecraft 1.8.8.

 */
public class RegistryAccessor_1_8_8 implements IRegistryAccessor {
    /** {@inheritDoc} */
    @Override
    public Enchantment getEnchantment(String key) {
        Optional<XEnchantment> enchantment = XEnchantment.of(key);
        //noinspection OptionalIsPresent
        if (!enchantment.isPresent())
            return null;
        return enchantment.get().get();
    }

    /** {@inheritDoc} */
    @Override
    public PotionType getPotionType(String key) {
        Optional<XPotion> potion = XPotion.of(key);
        //noinspection OptionalIsPresent
        if (!potion.isPresent())
            return null;
        return potion.get().getPotionType();
    }

    /** {@inheritDoc} */
    @Override
    public PotionEffectType getPotionEffectType(String key) {
        Optional<XPotion> potion = XPotion.of(key);
        //noinspection OptionalIsPresent
        if (!potion.isPresent())
            return null;
        return potion.get().getPotionEffectType();
    }

    /** {@inheritDoc} */
    @Override
    public EntityType getEntityType(String key) {
        return EntityType.valueOf(key);
    }
}
