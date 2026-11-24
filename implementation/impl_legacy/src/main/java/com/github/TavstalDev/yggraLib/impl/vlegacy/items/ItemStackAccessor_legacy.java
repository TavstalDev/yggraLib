package com.github.TavstalDev.yggraLib.impl.vlegacy.items;

import com.github.TavstalDev.yggraLib.bukkit.items.IItemStackAccessor;
import com.github.TavstalDev.yggraLib.bukkit.items.IRegistryAccessor;
import com.github.TavstalDev.yggraLib.bukkit.utils.AdventureUtils;
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

    /** {@inheritDoc} */
    @Override
    public @Nullable String getDisplayName(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasDisplayName())
            return null;

        return meta.getDisplayName();
    }

    /** {@inheritDoc} */
    @Override
    public @Nullable Component getDisplayNameComp(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasDisplayName())
            return null;

        return AdventureUtils.parse(meta.getDisplayName(), true);
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayName(ItemStack itemStack, String displayName) {
        if (displayName == null)
        {
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(null);
            itemStack.setItemMeta(meta);
            return;
        }
        setDisplayName(itemStack, AdventureUtils.parse(displayName));
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public List<String> getLore(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;
        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasLore())
            return null;
        return meta.getLore();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void setLore(ItemStack itemStack, List<String> lore) {
       List<Component> comps = new ArrayList<>();
       for (String line : lore) {
           comps.add(AdventureUtils.parse(line));
       }
       setLoreComp(itemStack, comps);
    }

    /** {@inheritDoc} */
    @Override
    public void setLoreComp(ItemStack itemStack, List<Component> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        ArrayList<String> result = new ArrayList<>();
        for (Component line : lore) {
            result.add(AdventureUtils.toLegacy(line));
        }
        meta.setLore(result);
        itemStack.setItemMeta(meta);
    }

    /** {@inheritDoc} */
    @Override
    public int getDurability(ItemStack itemStack) {
        return itemStack.getDurability();
    }

    /** {@inheritDoc} */
    @Override
    public void setDurability(ItemStack itemStack, int durability) {
        short dur = 0;
        if (durability > Short.MAX_VALUE)
            dur = Short.MAX_VALUE;
        else if (durability > 0)
            dur = (short)durability;
        itemStack.setDurability(dur);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> getEnchants(ItemStack itemStack) {
        Map<String, Integer> enchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet())
            enchantments.put(entry.getKey().getName(), entry.getValue());
        return enchantments;
    }


    /** {@inheritDoc} */
    @Override
    public void addEnchant(ItemStack itemStack, String name, int level) {
        Enchantment enchantment = registryAccessor.getEnchantment(name);
        if (enchantment == null)
            return;
        itemStack.addEnchantment(enchantment, level);
    }

    /** {@inheritDoc} */
    @Override
    public void clearEnchants(ItemStack itemStack) {
        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet())
            itemStack.removeEnchantment(entry.getKey());
    }

    /** {@inheritDoc} */
    @Override
    public Set<ItemFlag> getHiddenFlags(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return Collections.emptySet();
        ItemMeta meta = itemStack.getItemMeta();
        return meta.getItemFlags();
    }

    /** {@inheritDoc} */
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


    /** {@inheritDoc} */
    @Override
    public void hide(ItemStack itemStack, ItemFlag... flags) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(flags);
        itemStack.setItemMeta(meta);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getAmount(ItemStack itemStack) {
        return itemStack.getAmount();
    }

    /** {@inheritDoc} */
    @Override
    public void setAmount(ItemStack itemStack, int amount) {
        itemStack.setAmount(amount);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getCustomModelData(ItemStack itemStack) {
        // In 1.8.8 it is not supported
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setCustomModelData(ItemStack itemStack, int customModelData) {
        // In 1.8.8 it is not supported
    }
}
