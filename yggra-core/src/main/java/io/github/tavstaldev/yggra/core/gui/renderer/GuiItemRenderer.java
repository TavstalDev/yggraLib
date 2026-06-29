package io.github.tavstaldev.yggra.core.gui.renderer;

import io.github.tavstaldev.yggra.core.gui.GuiButton;
import io.github.tavstaldev.yggra.core.gui.GuiDupeDetector;
import io.github.tavstaldev.yggra.core.services.TranslationService;
import io.github.tavstaldev.yggra.items.api.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class that renders GUI buttons into ItemStack representations.
 */
public final class GuiItemRenderer {

    /**
     * Renders a GuiButton into an ItemStack, applying display name, lore, dupe protection,
     * and optional head textures.
     *
     * @param button     The button to render.
     * @param player     The player viewing the GUI.
     * @param translator The translation service for localization.
     * @return The rendered ItemStack.
     */
    public static ItemStack render(GuiButton button,
                            Player player,
                            TranslationService translator) {

        ItemStack itemStack = button.getDisplayItem();
        ItemMeta meta = itemStack.getItemMeta();
        String buttonTitle = button.getTitle();
        if (buttonTitle != null) {
            if (button.shouldTranslateTitle()) {
                meta.displayName(AdventureUtils.translateColors(translator.localize(player, buttonTitle), true));
            } else {
                meta.displayName(AdventureUtils.translateColors(buttonTitle, true));
            }
        }

        List<Component> lore = new ArrayList<>();
        var buttonLore = button.getLore();
        if (buttonLore != null) {
            if (button.shouldTranslateLore() && !buttonLore.isEmpty()) {
                String loreKey = buttonLore.getFirst();
                List<String> loreLines = translator.localizeList(player, loreKey);
                for (String line : loreLines) {
                    lore.add(AdventureUtils.translateColors(line, true));
                }
            } else {
                for (String line : buttonLore) {
                    if (line == null)
                        continue;
                    lore.add(AdventureUtils.translateColors(line, true));
                }
            }
        }
        meta.lore(lore);

        meta.getPersistentDataContainer().set(GuiDupeDetector.getDupeProtectedKey(), PersistentDataType.BOOLEAN, true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
