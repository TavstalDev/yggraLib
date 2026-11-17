package io.github.tavstaldev.yggraLib.impl.v1_21.items;

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
 * Implementation of ItemStackAccessor for Minecraft 1.21.
 */
public class ItemStackAccessor_1_21 implements IItemStackAccessor {

    private final IRegistryAccessor registryAccessor;

    /**
     * Creates a new ItemStackAccessor_1_21 instance.
     * @param registryAccessor the registryAccessor value
     */
    public ItemStackAccessor_1_21(IRegistryAccessor registryAccessor) {
        this.registryAccessor = registryAccessor;
    }

    @Override
    public @Nullable String getDisplayName(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasDisplayName())
            return null;

        Component name = meta.displayName();
        if (name == null)
            return null;

        return AdventureUtils.toLegacy(name);
    }

    @Override
    public @Nullable Component getDisplayNameComp(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasDisplayName())
            return null;

        return meta.displayName();
    }
    /**
     * Sets the value for setDisplayName.
     * @param itemStack the itemStack value
     * @param displayName the displayName value
     */

    @Override
    public void setDisplayName(ItemStack itemStack, String displayName) {
        ItemMeta meta = itemStack.getItemMeta();
        if (displayName == null)
        {
            meta.displayName(null);
            return;
        }
        meta.displayName(AdventureUtils.parse(displayName, true));
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
        meta.displayName(displayName);
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
            return Collections.emptyList();

        List<Component> comps = meta.lore();
        if (comps == null)
            return null;

        List<String> lore = new ArrayList<>();
        for (Component line : comps)
            lore.add(AdventureUtils.toLegacy(line));
        return lore;
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
            return Collections.emptyList();
        return meta.lore();
    }
    /**
     * Sets the value for setLore.
     * @param itemStack the itemStack value
     * @param lore the lore value
     */

    @Override
    public void setLore(ItemStack itemStack, List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        List<Component> loreList = new ArrayList<>();
        for (String line : lore) {
            loreList.add(AdventureUtils.parse(line, true));
        }
        meta.lore(loreList);
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
        meta.lore(lore);
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
            enchantments.put(entry.getKey().getKey().asString(), entry.getValue());
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
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_ARMOR_TRIM,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_STORED_ENCHANTS
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
        if (!itemStack.hasItemMeta())
            return 0;

        ItemMeta meta = itemStack.getItemMeta();
        return meta.getCustomModelData();
    }
    /**
     * Sets the value for setCustomModelData.
     * @param itemStack the itemStack value
     * @param customModelData the customModelData value
     */

    @Override
    public void setCustomModelData(ItemStack itemStack, int customModelData) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(customModelData);
        itemStack.setItemMeta(meta);
    }
}
