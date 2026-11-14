package io.github.tavstaldev.yggraLib.impl.vlegacy.services;

import io.github.tavstaldev.yggraLib.api.localization.ITranslationService;
import io.github.tavstaldev.yggraLib.bukkit.gui.GuiButton;
import io.github.tavstaldev.yggraLib.bukkit.gui.IGuiRenderer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GuiRenderer_legacy implements IGuiRenderer {
    @Override
    public ItemStack render(GuiButton button,
                            Player player,
                            ITranslationService translator) {

        ItemStack itemStack = button.getDisplayItem();
        ItemMeta meta = itemStack.getItemMeta();
        String buttonTitle = button.getTitle();
        if (buttonTitle != null) {
            if (button.shouldTranslateTitle()) {
                meta.setDisplayName(translator.localize(player.getUniqueId(), buttonTitle));
            } else {
                meta.setDisplayName(buttonTitle);
            }
        }

        List<String> lore = new ArrayList<>();
        List<String> buttonLore = button.getLore();
        if (buttonLore != null) {
            if (button.shouldTranslateLore() && !buttonLore.isEmpty()) {
                String loreKey = buttonLore.get(0);
                List<String> loreLines = translator.localizeList(player.getUniqueId(), loreKey);
                lore.addAll(loreLines);
            } else {
                for (String line : buttonLore) {
                    if (line == null)
                        continue;
                    lore.add(line);
                }
            }
        }
        meta.setLore(lore);

        //meta.getPersistentDataContainer().set(GuiDupeDetector.getDupeProtectedKey(), PersistentDataType.BOOLEAN, true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
