package io.github.tavstaldev.yggraLib.impl.vlegacy.items;

import io.github.tavstaldev.yggraLib.bukkit.items.IItemStackAccessor;
import io.github.tavstaldev.yggraLib.bukkit.items.IRegistryAccessor;
import io.github.tavstaldev.yggraLib.bukkit.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Legacy implementation of ItemStackAccessor for older Minecraft versions.
 */
public class ItemStackAccessor_legacy implements IItemStackAccessor {

    private final IRegistryAccessor registryAccessor;

    /**
     * Creates a new ItemStackAccessor_legacy instance.
     * @param registryAccessor the registryAccessor value
     */
    public ItemStackAccessor_legacy(IRegistryAccessor registryAccessor) {
        this.registryAccessor = registryAccessor;
    }

    @Override
    public @Nullable String getDisplayName(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasDisplayName())
            return null;

        return meta.getDisplayName();
    }

    @Override
    public @Nullable Component getDisplayNameComp(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasDisplayName())
            return null;

        return AdventureUtils.parse(meta.getDisplayName(), true);
    }
    /**
     * Sets the value for setDisplayName.
     * @param itemStack the itemStack value
     * @param displayName the displayName value
     */

    @Override
    public void setDisplayName(ItemStack itemStack, String displayName) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        itemStack.setItemMeta(meta);
    }
    /**
     * Sets the value for setDisplayName.
     * @param itemStack the itemStack value
     * @param displayName the displayName value
     */

    @Override
    public void setDisplayName(ItemStack itemStack, Component displayName) {
        ItemMeta meta = itemStack.getItemMeta();
        if (displayName == null) {
            meta.setDisplayName(null);
            itemStack.setItemMeta(meta);
            return;
        }
        String rawName = AdventureUtils.toLegacy(displayName);
        meta.setDisplayName(rawName);
        itemStack.setItemMeta(meta);
    }
    /**
     * Returns the value for getLore.
     * @param itemStack the itemStack value
     * @return the result
     */

    @Override
    public List<String> getLore(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;
        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasLore())
            return null;
        return meta.getLore();
    }
    /**
     * Returns the value for getLoreComp.
     * @param itemStack the itemStack value
     * @return the result
     */

    @Override
    public List<Component> getLoreComp(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;
        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasLore())
            return null;
        List<Component> result = new ArrayList<>();
        for (String line : meta.getLore())
            result.add(AdventureUtils.parse(line, true));
        return result;
    }
    /**
     * Sets the value for setLore.
     * @param itemStack the itemStack value
     * @param lore the lore value
     */

    @Override
    public void setLore(ItemStack itemStack, List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }
    /**
     * Sets the value for setLoreComp.
     * @param itemStack the itemStack value
     * @param lore the lore value
     */

    @Override
    public void setLoreComp(ItemStack itemStack, List<Component> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        ArrayList<String> result = new ArrayList<String>();
        for (Component line : lore) {
            result.add(AdventureUtils.toLegacy(line));
        }
        meta.setLore(result);
        itemStack.setItemMeta(meta);
    }
    /**
     * Returns the value for getEnchants.
     * @param itemStack the itemStack value
     * @return the result
     */

    @Override
    public Map<String, Integer> getEnchants(ItemStack itemStack) {
        Map<String, Integer> enchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet())
            enchantments.put(entry.getKey().getName(), entry.getValue());
        return enchantments;
    }
    /**
     * Handles the addEnchant operation.
     * @param itemStack the itemStack value
     * @param name the name value
     * @param level the level value
     */

    @Override
    public void addEnchant(ItemStack itemStack, String name, int level) {
        Enchantment enchantment = registryAccessor.getEnchantment(name);
        if (enchantment == null)
            return;
        itemStack.addEnchantment(enchantment, level);
    }
    /**
     * Handles the clearEnchants operation.
     * @param itemStack the itemStack value
     */

    @Override
    public void clearEnchants(ItemStack itemStack) {
        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet())
            itemStack.removeEnchantment(entry.getKey());
    }
    /**
     * Returns the value for getHiddenFlags.
     * @param itemStack the itemStack value
     * @return the result
     */

    @Override
    public Set<ItemFlag> getHiddenFlags(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return Collections.emptySet();
        ItemMeta meta = itemStack.getItemMeta();
        return meta.getItemFlags();
    }
    /**
     * Handles the hideAllFlags operation.
     * @param itemStack the itemStack value
     */

    @Override
    public void hideAllFlags(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
        );
        itemStack.setItemMeta(meta);
    }
    /**
     * Handles the hide operation.
     * @param itemStack the itemStack value
     * @param flags the flags value
     */

    @Override
    public void hide(ItemStack itemStack, ItemFlag... flags) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(flags);
        itemStack.setItemMeta(meta);
    }
    /**
     * Returns the value for getAmount.
     * @param itemStack the itemStack value
     * @return the result
     */

    @Override
    public Integer getAmount(ItemStack itemStack) {
        return itemStack.getAmount();
    }
    /**
     * Sets the value for setAmount.
     * @param itemStack the itemStack value
     * @param amount the amount value
     */

    @Override
    public void setAmount(ItemStack itemStack, int amount) {
        itemStack.setAmount(amount);
    }
    /**
     * Returns the value for getCustomModelData.
     * @param itemStack the itemStack value
     * @return the result
     */

    @Override
    public Integer getCustomModelData(ItemStack itemStack) {
        // In 1.8.8 it is not supported
        return 0;
    }
    /**
     * Sets the value for setCustomModelData.
     * @param itemStack the itemStack value
     * @param customModelData the customModelData value
     */

    @Override
    public void setCustomModelData(ItemStack itemStack, int customModelData) {
        // In 1.8.8 it is not supported
    }
}
