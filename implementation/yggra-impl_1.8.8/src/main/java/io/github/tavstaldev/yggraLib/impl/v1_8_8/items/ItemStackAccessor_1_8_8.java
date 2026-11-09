package io.github.tavstaldev.yggraLib.impl.v1_8_8.items;

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

public class ItemStackAccessor_1_8_8 implements IItemStackAccessor {

    private final IRegistryAccessor registryAccessor;

    public ItemStackAccessor_1_8_8(IRegistryAccessor registryAccessor) {
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

    @Override
    public void setDisplayName(ItemStack itemStack, String displayName) {
        if (!itemStack.hasItemMeta())
            return;

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
    }

    @Override
    public void setDisplayName(ItemStack itemStack, Component displayName) {
        if (!itemStack.hasItemMeta())
            return;

        ItemMeta meta = itemStack.getItemMeta();
        String rawName = AdventureUtils.toLegacy(displayName);
        meta.setDisplayName(rawName);
    }

    @Override
    public List<String> getLore(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasLore())
            return Collections.emptyList();
        return meta.getLore();
    }

    @Override
    public List<Component> getLoreComp(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasLore())
            return Collections.emptyList();
        List<Component> result = new ArrayList<>();
        for (String line : meta.getLore())
            result.add(AdventureUtils.parse(line, true));

        return result;
    }

    @Override
    public void setLore(ItemStack itemStack, List<String> lore) {
        if (!itemStack.hasItemMeta())
            return;

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasLore())
            return;

        meta.setLore(lore);
    }

    @Override
    public void setLoreComp(ItemStack itemStack, List<Component> lore) {
        if (!itemStack.hasItemMeta())
            return;

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasLore())
            return;

        List<String> result = new ArrayList<>();
        for (Component line : lore)
            result.add(AdventureUtils.toLegacy(line));
        meta.setLore(result);
    }

    @Override
    public Map<String, Integer> getEnchants(ItemStack itemStack) {
        Map<String, Integer> enchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet())
            enchantments.put(entry.getKey().getName(), entry.getValue());
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
        if (!itemStack.hasItemMeta())
            return;

        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_UNBREAKABLE
        );
    }

    @Override
    public void hide(ItemStack itemStack, ItemFlag... flags) {
        if (!itemStack.hasItemMeta())
            return;

        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(flags);
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
        // In 1.8.8 it is not supported
        return 0;
    }

    @Override
    public void setCustomModelData(ItemStack itemStack, int customModelData) {
        // In 1.8.8 it is not supported
    }
}
