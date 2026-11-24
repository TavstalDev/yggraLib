package com.github.TavstalDev.yggraLib.bukkit.gui;

import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Provides methods to register, unregister, and manage player menus.
 */
@SuppressWarnings("unused")
public interface IGuiService {
    /**
     * Registers a menu, loading its configuration.
     *
     * @param menuBase The menu to register.
     */
    void register(GuiBase menuBase);

    /**
     * Unregisters a menu by its ID.
     *
     * @param id The menu ID.
     */
    void unregister(String id);

    /**
     * Unregisters a menu.
     *
     * @param menuBase The menu to unregister.
     */
    void unregister(GuiBase menuBase);

    /**
     * Unregisters all menus.
     */
    void unregisterAll();

    /**
     * Opens a menu for a player, reusing a cached menu if available.
     *
     * @param player the player to open the menu for
     * @param menuId the menu ID
     * @param args   optional arguments passed to the menu
     */
    void open(Player player, String menuId, @Nullable Object... args);

    /**
     * Closes the currently open menu for a player.
     *
     * @param player             The player to close the menu for.
     * @param openingAnotherMenu Whether another menu will be opened immediately afterward.
     */
    void close(Player player, boolean openingAnotherMenu);

    /**
     * Closes all open menus for all online players.
     */
    void closeAll();

    /**
     * Invalidates the cached menu data for a specific player.
     *
     * @param player The player whose cache to invalidate.
     */
    void invalidateCache(Player player);

    /**
     * Invalidates cached menu data for all online players.
     */
    void invalidateAllCaches();

    /**
     * Refreshes a cached menu for a player.
     *
     * @param player The player viewing the menu.
     * @param menuId The menu ID to refresh.
     */
    void refresh(Player player, String menuId);

    /**
     * Retrieves a cached menu for a player, if available.
     *
     * @param player The player whose menu to retrieve.
     * @param menuId The menu ID.
     * @return The cached BaseGui, or null.
     */
    @Nullable BaseGui getMenu(Player player, String menuId);

    /**
     * Checks whether a player currently has a menu open.
     *
     * @param player The player to check.
     * @return True if the player has an open menu.
     */
    boolean hasMenuOpen(Player player);
}
