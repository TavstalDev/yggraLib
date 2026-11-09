package io.github.tavstaldev.yggraLib.bukkit.items;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IItemStackAccessor {

    //#region DisplayName
    @Nullable String getDisplayName(ItemStack itemStack);

    @Nullable Component getDisplayNameComp(ItemStack itemStack);

    void setDisplayName(ItemStack itemStack, @Nullable String displayName);

    void setDisplayName(ItemStack itemStack, @Nullable Component displayName);

    //#endregion

    //#region Lore
    List<String> getLore(ItemStack itemStack);

    List<Component> getLoreComp(ItemStack itemStack);

    void setLore(ItemStack itemStack, List<String> lore);

    void setLoreComp(ItemStack itemStack, List<Component> lore);
    //#endregion


    Map<String, Integer> getEnchants(ItemStack itemStack);
    void addEnchant(ItemStack itemStack, String name, int level);
    void clearEnchants(ItemStack itemStack);


    Set<ItemFlag> getHiddenFlags(ItemStack itemStack);
    void hideAllFlags(ItemStack itemStack);
    void hide(ItemStack itemStack, ItemFlag... flags);


    Integer getAmount(ItemStack itemStack);
    void setAmount(ItemStack itemStack, int amount);


    Integer getCustomModelData(ItemStack itemStack);
    void setCustomModelData(ItemStack itemStack, int customModelData);
}
