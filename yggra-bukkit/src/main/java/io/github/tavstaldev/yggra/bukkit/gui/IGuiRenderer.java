package io.github.tavstaldev.yggra.bukkit.gui;

import io.github.tavstaldev.yggra.api.localization.ITranslationService;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IGuiRenderer {
    ItemStack render(GuiButton button,
                     Player player,
                     ITranslationService translator);
}
