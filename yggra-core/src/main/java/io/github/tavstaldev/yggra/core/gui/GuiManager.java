package io.github.tavstaldev.yggra.core.gui;

import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.menu.SGMenu;
import io.github.tavstaldev.yggra.core.YggraPlugin;
import io.github.tavstaldev.yggra.core.cache.ICache;
import io.github.tavstaldev.yggra.core.logger.YggraLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class GuiManager {
    private final YggraPlugin plugin;
    private final YggraLogger logger;
    private final ICache cache;
    @SuppressWarnings("FieldCanBeLocal")
    private final long CACHE_EXPIRATION_SECONDS = 600; // 10 minutes
    private final SpiGUI _spiGUI;
    public SpiGUI getSpiGUI() {
        return _spiGUI;
    }
    private final HashMap<String, GuiBase> _registeredMenus = new HashMap<>();
    private final HashMap<UUID, String> _playersWithOpenMenus = new HashMap<>();

    public GuiManager(YggraPlugin plugin) {
        this.plugin = plugin;
        this.cache = plugin.cache();
        this.logger = plugin.logger().withModule(this.getClass());
        _spiGUI = new SpiGUI(plugin);
    }

    public void register(GuiBase menuBase) {
        menuBase.load();
        _registeredMenus.put(menuBase.id, menuBase);
        logger.debug("Registered menu with ID: " + menuBase.id);
    }

    public void unregister(String id) {
        _registeredMenus.remove(id);
        logger.debug("Unregistered menu with ID: " + id);
    }

    public void unregister(GuiBase menuBase) {
        unregister(menuBase.id);
    }

    public void open(Player player, String menuId) {
        String cacheKey = getPlayerCacheKey(player);
        GuiBase menuBase = _registeredMenus.get(menuId);
        if (menuBase == null)
            return;

        @SuppressWarnings("unchecked")
        Map<String, SGMenu> playerMenus = cache.get(cacheKey, Map.class);
        if (playerMenus != null && playerMenus.containsKey(menuId)) {
            SGMenu existingMenu = playerMenus.get(menuId);
            player.openInventory(existingMenu.getInventory());
            menuBase.onOpen(player);
            _playersWithOpenMenus.put(player.getUniqueId(), menuId);
            return;
        }
        else if (playerMenus == null) {
            playerMenus = new HashMap<>();
        }

        SGMenu menu = menuBase.create(player);
        if (menu == null) {
            logger.error("Failed to create menu with ID: " + menuId + " for player: " + player.getName());
            return;
        }
        playerMenus.put(menuId, menu);
        cache.put(cacheKey, playerMenus, CACHE_EXPIRATION_SECONDS);
        player.openInventory(menu.getInventory());
        menuBase.onOpen(player);
        _playersWithOpenMenus.put(player.getUniqueId(), menuId);
    }

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

    public void closeAll() {
        for (UUID playerId : _playersWithOpenMenus.keySet()) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null && player.isOnline()) {
                close(player, false);
            }
        }
    }

    public void invalidateCache(Player player) {
        String cacheKey = getPlayerCacheKey(player);
        cache.evict(cacheKey);
    }

    public void invalidateAllCaches() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            invalidateCache(player);
        }
    }

    public void refresh(Player player, String menuId) {
        String cacheKey = getPlayerCacheKey(player);
        @SuppressWarnings("unchecked")
        Map<String, SGMenu> playerMenus = cache.get(cacheKey, Map.class);
        if (playerMenus == null || !playerMenus.containsKey(menuId))
            return;

        SGMenu menu = playerMenus.get(menuId);
        GuiBase menuBase = _registeredMenus.get(menuId);
        if (menuBase == null)
            return;

        menuBase.refresh(player, menu);
    }

    public @Nullable SGMenu getMenu(Player player, String menuId) {
        String cacheKey = getPlayerCacheKey(player);
        @SuppressWarnings("unchecked")
        Map<String, SGMenu> playerMenus = cache.get(cacheKey, Map.class);
        if (playerMenus != null && playerMenus.containsKey(menuId)) {
            return playerMenus.get(menuId);
        }
        return null;
    }

    public boolean hasMenuOpen(Player player) {
        return _playersWithOpenMenus.containsKey(player.getUniqueId());
    }

    private String getPlayerCacheKey(Player player) {
        return "menu_caches_" + player.getUniqueId();
    }
}
