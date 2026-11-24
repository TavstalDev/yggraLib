package com.github.TavstalDev.yggraLib.bukkit.items;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides version-safe access to common {@link ItemStack} properties.
 * <p>
 * Implementations handle differences between server versions and APIs.
 */
public interface IItemStackAccessor {

    /**
     * Gets the display name of an item as a plain string.
     *
     * @param itemStack the item to read from
     * @return the display name, or {@code null} if none is set
     */
    @Nullable String getDisplayName(ItemStack itemStack);

    /**
     * Gets the display name of an item as an Adventure component.
     *
     * @param itemStack the item to read from
     * @return the display name component, or {@code null} if none is set
     */
    @Nullable Component getDisplayNameComp(ItemStack itemStack);

    /**
     * Sets the display name of an item using a plain string.
     *
     * @param itemStack   the item to update
     * @param displayName the new display name, or {@code null} to clear it
     */
    void setDisplayName(ItemStack itemStack, @Nullable String displayName);

    /**
     * Sets the display name of an item using an Adventure component.
     *
     * @param itemStack   the item to update
     * @param displayName the new display name component, or {@code null} to clear it
     */
    void setDisplayName(ItemStack itemStack, @Nullable Component displayName);

    /**
     * Gets the lore of an item as plain strings.
     *
     * @param itemStack the item to read from
     * @return the lore lines, or an empty list if none are set
     */
    List<String> getLore(ItemStack itemStack);

    /**
     * Gets the lore of an item as Adventure components.
     *
     * @param itemStack the item to read from
     * @return the lore components, or an empty list if none are set
     */
    List<Component> getLoreComp(ItemStack itemStack);

    /**
     * Sets the lore of an item using plain strings.
     *
     * @param itemStack the item to update
     * @param lore      the lore lines to set
     */
    void setLore(ItemStack itemStack, List<String> lore);

    /**
     * Sets the lore of an item using Adventure components.
     *
     * @param itemStack the item to update
     * @param lore      the lore components to set
     */
    void setLoreComp(ItemStack itemStack, List<Component> lore);

    /**
     * Gets the damage (durability) value of an item.
     *
     * @param itemStack the item to read from
     * @return the damage value, or {@code -1} if the item cannot be damaged
     */
    int getDurability(ItemStack itemStack);

    /**
     * Sets the damage (durability) value of an item.
     * Items that cannot be damaged are left unchanged.
     *
     * @param itemStack  the item to update
     * @param durability the new damage value
     */
    void setDurability(ItemStack itemStack, int durability);

    /**
     * Gets the enchantments applied to an item.
     *
     * @param itemStack the item to read from
     * @return a map of enchantment keys to levels
     */
    Map<String, Integer> getEnchants(ItemStack itemStack);

    /**
     * Adds an enchantment to an item.
     *
     * @param itemStack the item to update
     * @param name      the enchantment key or name
     * @param level     the enchantment level
     */
    void addEnchant(ItemStack itemStack, String name, int level);

    /**
     * Removes all enchantments from an item.
     *
     * @param itemStack the item to update
     */
    void clearEnchants(ItemStack itemStack);

    /**
     * Gets the item flags that are currently hidden.
     *
     * @param itemStack the item to read from
     * @return the hidden item flags
     */
    Set<ItemFlag> getHiddenFlags(ItemStack itemStack);

    /**
     * Hides all supported item flags on an item.
     *
     * @param itemStack the item to update
     */
    void hideAllFlags(ItemStack itemStack);

    /**
     * Hides specific item flags on an item.
     *
     * @param itemStack the item to update
     * @param flags     the flags to hide
     */
    void hide(ItemStack itemStack, ItemFlag... flags);

    /**
     * Gets the stack amount of an item.
     *
     * @param itemStack the item to read from
     * @return the stack amount
     */
    Integer getAmount(ItemStack itemStack);

    /**
     * Sets the stack amount of an item.
     *
     * @param itemStack the item to update
     * @param amount    the new stack amount
     */
    void setAmount(ItemStack itemStack, int amount);

    /**
     * Gets the custom model data value of an item.
     *
     * @param itemStack the item to read from
     * @return the custom model data value, or {@code null} if none is set
     */
    Integer getCustomModelData(ItemStack itemStack);

    /**
     * Sets the custom model data value of an item.
     *
     * @param itemStack     the item to update
     * @param customModelData the custom model data value
     */
    void setCustomModelData(ItemStack itemStack, int customModelData);
}
