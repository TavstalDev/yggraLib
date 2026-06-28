package io.github.tavstaldev.yggra.core.gui;

import dev.triumphteam.gui.guis.BaseGui;
import io.github.tavstaldev.yggra.core.YggraPlugin;
import io.github.tavstaldev.yggra.core.cache.ICache;
import io.github.tavstaldev.yggra.core.logger.YggraLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages GUI menu registration, caching, opening, closing, and refresh for players.
 */
public class GuiManager {
    private final YggraPlugin<?> plugin;
    private final YggraLogger logger;
    private final ICache cache;
    @SuppressWarnings("FieldCanBeLocal")
    private final long CACHE_EXPIRATION_SECONDS = 600; // 10 minutes
    private final HashMap<String, GuiBase> _registeredMenus = new HashMap<>();
    private final HashMap<UUID, String> _playersWithOpenMenus = new HashMap<>();

    /**
     * Constructs a new GuiManager.
     *
     * @param plugin The plugin instance.
     */
    public GuiManager(YggraPlugin<?> plugin) {
        this.plugin = plugin;
        this.cache = plugin.cache();
        this.logger = plugin.logger().withModule(this.getClass());
    }

    /**
     * Registers a menu, loading its configuration.
     *
     * @param menuBase The menu to register.
     */
    public void register(GuiBase menuBase) {
        menuBase.load();
        _registeredMenus.put(menuBase.id, menuBase);
        logger.debug("Registered menu with ID: " + menuBase.id);
    }

    /**
     * Unregisters a menu by its ID.
     *
     * @param id The menu ID.
     */
    public void unregister(String id) {
        _registeredMenus.remove(id);
        logger.debug("Unregistered menu with ID: " + id);
    }

    /**
     * Unregisters a menu.
     *
     * @param menuBase The menu to unregister.
     */
    public void unregister(GuiBase menuBase) {
        unregister(menuBase.id);
    }

    /**
     * Opens a menu for a player, restoring from cache if available.
     *
     * @param player The player to open the menu for.
     * @param menuId The menu ID.
     * @param args Args passed to the opened menu
     */
    public void open(Player player, String menuId, @Nullable Object... args) {
        String cacheKey = getPlayerCacheKey(player);
        GuiBase menuBase = _registeredMenus.get(menuId);
        if (menuBase == null)
            return;

        @SuppressWarnings("unchecked")
        Map<String, BaseGui> playerMenus = cache.get(cacheKey, Map.class);
        if (playerMenus != null && playerMenus.containsKey(menuId)) {
            BaseGui existingMenu = playerMenus.get(menuId);
            existingMenu.open(player);
            menuBase.onOpen(player, args);
            _playersWithOpenMenus.put(player.getUniqueId(), menuId);
            return;
        }
        else if (playerMenus == null) {
            playerMenus = new HashMap<>();
        }

        BaseGui menu = menuBase.create(player);
        if (menu == null) {
            logger.error("Failed to create menu with ID: " + menuId + " for player: " + player.getName());
            return;
        }
        playerMenus.put(menuId, menu);
        cache.put(cacheKey, playerMenus, CACHE_EXPIRATION_SECONDS);
        menu.open(player);
        menuBase.onOpen(player, args);
        _playersWithOpenMenus.put(player.getUniqueId(), menuId);
    }

    /**
     * Closes the currently open menu for a player.
     *
     * @param player             The player to close the menu for.
     * @param openingAnotherMenu Whether another menu will be opened immediately afterward.
     */
    public void close(Player player, boolean openingAnotherMenu) {
        player.closeInventory();
        String openedId = _playersWithOpenMenus.get(player.getUniqueId());
        if (openedId != null) {
            GuiBase menuBase = _registeredMenus.get(openedId);
            if (menuBase != null) {
                menuBase.onClose(player);
            }
        }

        if (openingAnotherMenu)
            return;
        _playersWithOpenMenus.remove(player.getUniqueId());
    }

    /**
     * Closes all open menus for all online players.
     */
    public void closeAll() {
        for (UUID playerId : _playersWithOpenMenus.keySet()) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null && player.isOnline()) {
                close(player, false);
            }
        }
    }

    /**
     * Invalidates the cached menu data for a specific player.
     *
     * @param player The player whose cache to invalidate.
     */
    public void invalidateCache(Player player) {
        String cacheKey = getPlayerCacheKey(player);
        cache.evict(cacheKey);
    }

    /**
     * Invalidates cached menu data for all online players.
     */
    public void invalidateAllCaches() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            invalidateCache(player);
        }
    }

    /**
     * Refreshes a cached menu for a player.
     *
     * @param player The player viewing the menu.
     * @param menuId The menu ID to refresh.
     */
    public void refresh(Player player, String menuId) {
        String cacheKey = getPlayerCacheKey(player);
        @SuppressWarnings("unchecked")
        Map<String, BaseGui> playerMenus = cache.get(cacheKey, Map.class);
        if (playerMenus == null || !playerMenus.containsKey(menuId))
            return;

        BaseGui menu = playerMenus.get(menuId);
        GuiBase menuBase = _registeredMenus.get(menuId);
        if (menuBase == null)
            return;

        menuBase.refresh(player, menu);
    }

    /**
     * Retrieves a cached menu for a player, if available.
     *
     * @param player The player whose menu to retrieve.
     * @param menuId The menu ID.
     * @return The cached BaseGui, or null.
     */
    public @Nullable BaseGui getMenu(Player player, String menuId) {
        String cacheKey = getPlayerCacheKey(player);
        @SuppressWarnings("unchecked")
        Map<String, BaseGui> playerMenus = cache.get(cacheKey, Map.class);
        if (playerMenus != null && playerMenus.containsKey(menuId)) {
            return playerMenus.get(menuId);
        }
        return null;
    }

    /**
     * Checks whether a player currently has a menu open.
     *
     * @param player The player to check.
     * @return True if the player has an open menu.
     */
    public boolean hasMenuOpen(Player player) {
        return _playersWithOpenMenus.containsKey(player.getUniqueId());
    }

    /**
     * Builds a unique cache key for the given player.
     *
     * @param player The player.
     * @return The cache key string.
     */
    private String getPlayerCacheKey(Player player) {
        return "menu_caches_" + player.getUniqueId();
    }
}
