package com.github.TavstalDev.yggraLib.bukkit.gui;

import com.github.TavstalDev.yggraLib.api.localization.ITranslationService;
import com.github.TavstalDev.yggraLib.bukkit.items.IItemStackAccessor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Renders a {@link GuiButton} as a display {@link ItemStack} for a player.
 */
public interface IGuiRenderer {

    /**
     * Creates the item to show for a button in a menu.
     *
     * @param button            the button definition
     * @param player            the player viewing the menu
     * @param translator        the translation service
     * @param itemStackAccessor the item stack accessor
     * @return the rendered item stack
     */
    ItemStack render(GuiButton button,
                     Player player,
                     ITranslationService translator,
                     IItemStackAccessor itemStackAccessor);
}
