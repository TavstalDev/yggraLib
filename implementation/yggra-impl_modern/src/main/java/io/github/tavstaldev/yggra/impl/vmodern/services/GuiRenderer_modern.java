package io.github.tavstaldev.yggra.impl.vmodern.services;

import io.github.tavstaldev.yggra.api.localization.ITranslationService;
import io.github.tavstaldev.yggra.bukkit.gui.GuiButton;
import io.github.tavstaldev.yggra.bukkit.gui.IGuiRenderer;
import io.github.tavstaldev.yggra.bukkit.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GuiRenderer_modern implements IGuiRenderer {
    @Override
    public ItemStack render(GuiButton button,
                            Player player,
                            ITranslationService translator) {

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

        List<Component> lore = new ArrayList<>();
        var buttonLore = button.getLore();
        if (buttonLore != null) {
            if (button.shouldTranslateLore() && !buttonLore.isEmpty()) {
                String loreKey = buttonLore.get(0);
                List<String> loreLines = translator.localizeList(player.getUniqueId(), loreKey);
                for (String line : loreLines) {
                    lore.add(AdventureUtils.parse(line, true));
                }
            } else {
                for (String line : buttonLore) {
                    if (line == null)
                        continue;
                    lore.add(AdventureUtils.parse(line, true));
                }
            }
        }
        meta.lore(lore);

        //meta.getPersistentDataContainer().set(GuiDupeDetector.getDupeProtectedKey(), PersistentDataType.BOOLEAN, true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
