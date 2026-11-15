package io.github.tavstaldev.yggraLib.impl.vlegacy.services;

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

public class GuiRenderer_legacy implements IGuiRenderer {
    @Override
    public ItemStack render(GuiButton button, Player player, ITranslationService translator, IItemStackAccessor itemStackAccessor) {
        ItemStack itemStack = button.getDisplayItem();
        String buttonTitle = button.getTitle();
        if (buttonTitle != null) {
            if (button.shouldTranslateTitle()) {
                itemStackAccessor.setDisplayName(itemStack, AdventureUtils.parse(translator.localize(player.getUniqueId(), buttonTitle)));
            } else {
                itemStackAccessor.setDisplayName(itemStack, AdventureUtils.parse(buttonTitle));
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
        itemStackAccessor.setLoreComp(itemStack, lore);
        return itemStack;
    }
}
