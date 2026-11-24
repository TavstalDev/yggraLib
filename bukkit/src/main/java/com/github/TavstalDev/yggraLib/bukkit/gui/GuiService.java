package com.github.TavstalDev.yggraLib.bukkit.gui;

import dev.triumphteam.gui.guis.BaseGui;
import com.github.TavstalDev.yggraLib.api.IYggraPlugin;
import com.github.TavstalDev.yggraLib.api.cache.ICache;
import com.github.TavstalDev.yggraLib.api.logger.YggraLogger;
import dev.triumphteam.gui.guis.GuiListener;
import dev.triumphteam.gui.guis.InteractionModifierListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages GUI menu registration, caching, opening, closing, and refresh for players.
 */
public class GuiService implements IGuiService {
    private final YggraLogger logger;
    private final ICache cache;
    @SuppressWarnings("FieldCanBeLocal")
    private final long CACHE_EXPIRATION_SECONDS = 600; // 10 minutes
    private final HashMap<String, GuiBase> _registeredMenus = new HashMap<>();
    private final HashMap<UUID, String> _playersWithOpenMenus = new HashMap<>();

    /**
     * Creates a new GUI service and registers GUI listeners when needed.
     *
     * @param plugin the plugin instance that owns this service
     * @param cache  the cache used for menu data
     */
    public GuiService(IYggraPlugin plugin, ICache cache) {
        this.cache = cache;
        this.logger = plugin.logger().withModule(this.getClass());

        if (!plugin.isGuiInitialized()) {
            JavaPlugin javaPlugin = (JavaPlugin)plugin;
            Bukkit.getPluginManager().registerEvents(new InteractionModifierListener(), javaPlugin);
            Bukkit.getPluginManager().registerEvents(new GuiListener(), javaPlugin);
            plugin.setIsGuiInitialized(true);
        }
    }

    /** {@inheritDoc} */
    public void register(GuiBase menuBase) {
        _registeredMenus.put(menuBase.id, menuBase);
        logger.debug("Registered menu with ID: " + menuBase.id);
    }

    /** {@inheritDoc} */
    public void unregister(String id) {
        _registeredMenus.remove(id);
        logger.debug("Unregistered menu with ID: " + id);
    }

    /** {@inheritDoc} */
    public void unregister(GuiBase menuBase) {
        unregister(menuBase.id);
    }

    /** {@inheritDoc} */
    public void unregisterAll() {
       _registeredMenus.clear();
       logger.debug("Unregistered all menus.");
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void closeAll() {
        for (UUID playerId : _playersWithOpenMenus.keySet()) {
            Player player = Bukkit.getServer().getPlayer(playerId);
            if (player != null && player.isOnline()) {
                close(player, false);
            }
        }
    }

    /** {@inheritDoc} */
    public void invalidateCache(Player player) {
        String cacheKey = getPlayerCacheKey(player);
        cache.evict(cacheKey);
    }

    /** {@inheritDoc} */
    public void invalidateAllCaches() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            invalidateCache(player);
        }
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public @Nullable BaseGui getMenu(Player player, String menuId) {
        String cacheKey = getPlayerCacheKey(player);
        @SuppressWarnings("unchecked")
        Map<String, BaseGui> playerMenus = cache.get(cacheKey, Map.class);
        if (playerMenus != null && playerMenus.containsKey(menuId)) {
            return playerMenus.get(menuId);
        }
        return null;
    }

    /** {@inheritDoc} */
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
