package io.github.tavstaldev.yggraLib.impl.vmodern.services;

import io.github.tavstaldev.yggraLib.api.localization.ITranslationService;
import io.github.tavstaldev.yggraLib.bukkit.gui.GuiButton;
import io.github.tavstaldev.yggraLib.bukkit.gui.IGuiRenderer;
import io.github.tavstaldev.yggraLib.bukkit.items.IItemStackAccessor;
import io.github.tavstaldev.yggraLib.bukkit.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Modern implementation of GuiRenderer for newer Minecraft versions.
 */
public class GuiRenderer_modern implements IGuiRenderer {
    /**
     * Handles the render operation.
     * @param button the button value
     * @param player the player value
     * @param translator the translator value
     * @param itemStackAccessor the itemStackAccessor value
     * @return the result
     */
    @Override
    public ItemStack render(GuiButton button, Player player, ITranslationService translator, IItemStackAccessor itemStackAccessor) {
        ItemStack itemStack = button.getDisplayItem();
        ItemMeta meta = itemStack.getItemMeta();
        String buttonTitle = button.getTitle();
        if (buttonTitle != null) {
            if (button.shouldTranslateTitle()) {
                meta.displayName(AdventureUtils.parse(translator.localize(player.getUniqueId(), buttonTitle), true));
            } else {
                meta.displayName(AdventureUtils.parse(buttonTitle, true));
            }
        }
        ArrayList<Component> lore = new ArrayList<Component>();
        List<String> buttonLore = button.getLore();
        if (buttonLore != null) {
            if (button.shouldTranslateLore() && !buttonLore.isEmpty()) {
                String loreKey = buttonLore.get(0);
                List<String> loreLines = translator.localizeList(player.getUniqueId(), loreKey);
                for (String line : loreLines) {
                    lore.add(AdventureUtils.parse(line, true));
                }
            } else {
                for (String line : buttonLore) {
                    if (line == null) continue;
                    lore.add(AdventureUtils.parse(line, true));
                }
            }
        }
        meta.lore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
