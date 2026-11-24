package com.github.TavstalDev.yggraLib.impl.v1_21.items;

import com.github.TavstalDev.yggraLib.bukkit.items.IItemStackAccessor;
import com.github.TavstalDev.yggraLib.bukkit.items.IRegistryAccessor;
import com.github.TavstalDev.yggraLib.bukkit.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
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

    @Override
    public void setDisplayName(ItemStack itemStack, Component displayName) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(displayName);
        itemStack.setItemMeta(meta);
    }

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

    @Override
    public List<Component> getLoreComp(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasLore())
            return Collections.emptyList();
        return meta.lore();
    }

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

    @Override
    public void setLoreComp(ItemStack itemStack, List<Component> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.lore(lore);
        itemStack.setItemMeta(meta);
    }

    @Override
    public int getDurability(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof Damageable damageable) {
            return damageable.getDamage();
        }
        return -1;
    }

    @Override
    public void setDurability(ItemStack itemStack, int durability) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof Damageable damageable)
            damageable.setDamage(durability);
    }

    @Override
    public Map<String, Integer> getEnchants(ItemStack itemStack) {
        Map<String, Integer> enchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet())
            enchantments.put(entry.getKey().getKey().asString(), entry.getValue());
        return enchantments;
    }

    @Override
    public void addEnchant(ItemStack itemStack, String name, int level) {
        Enchantment enchantment = registryAccessor.getEnchantment(name);
        if (enchantment == null)
            return;
        itemStack.addEnchantment(enchantment, level);
    }

    @Override
    public void clearEnchants(ItemStack itemStack) {
        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet())
            itemStack.removeEnchantment(entry.getKey());
    }

    @Override
    public Set<ItemFlag> getHiddenFlags(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return Collections.emptySet();

        ItemMeta meta = itemStack.getItemMeta();
        return meta.getItemFlags();
    }

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

    @Override
    public void hide(ItemStack itemStack, ItemFlag... flags) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(flags);
        itemStack.setItemMeta(meta);
    }

    @Override
    public Integer getAmount(ItemStack itemStack) {
        return itemStack.getAmount();
    }

    @Override
    public void setAmount(ItemStack itemStack, int amount) {
        itemStack.setAmount(amount);
    }

    @Override
    public Integer getCustomModelData(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return 0;

        ItemMeta meta = itemStack.getItemMeta();
        return meta.getCustomModelData();
    }

    @Override
    public void setCustomModelData(ItemStack itemStack, int customModelData) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(customModelData);
        itemStack.setItemMeta(meta);
    }
}
