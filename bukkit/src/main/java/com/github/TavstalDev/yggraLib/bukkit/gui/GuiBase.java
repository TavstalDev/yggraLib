package com.github.TavstalDev.yggraLib.bukkit.gui;

import com.github.TavstalDev.yggraLib.api.IYggraPlugin;
import com.github.TavstalDev.yggraLib.api.gui.SlotParser;
import com.github.TavstalDev.yggraLib.api.localization.ITranslationService;
import com.github.TavstalDev.yggraLib.api.logger.YggraLogger;
import com.github.TavstalDev.yggraLib.bukkit.items.IItemSerializer;
import com.github.TavstalDev.yggraLib.bukkit.items.IItemStackAccessor;
import com.github.TavstalDev.yggraLib.bukkit.utils.AdventureUtils;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Abstract base class for all GUI menus. Handles menu creation, button resolution,
 * dynamic slot parsing, and configuration persistence.
 */
public abstract class GuiBase {
    /** The configuration file used by this menu. */
    protected final File file;
    /** The YAML configuration loaded from the menu file. */
    protected final YamlConfiguration yaml;
    /** The plugin instance that owns this menu. */
    protected final IYggraPlugin plugin;
    /** Logger used by this menu. */
    protected final YggraLogger logger;
    /** Translation service used for localized text. */
    protected final ITranslationService translator;
    /** Renderer used to create button items. */
    protected final IGuiRenderer guiRenderer;
    /** Accessor used to modify item stack properties. */
    protected final IItemStackAccessor itemStackAccessor;
    /** Serializer used for menu item data. */
    protected final IItemSerializer itemSerializer;
    /** The unique identifier of this menu. */
    public final String id;
    /** The configured menu title. */
    protected String menuTitle;
    /** Whether the menu title should be localized. */
    protected boolean isTitleTranslated;
    /** The number of rows in the menu. */
    protected int menuSize;
    /** Parsed dynamic slot groups for this menu. */
    protected HashMap<String, List<Integer>> dynamicSlots;
    /** The buttons configured for this menu. */
    protected HashSet<GuiButton> buttons;

    /**
     * Creates a GUI menu from configuration values or defaults.
     *
     * @param id                  the unique menu identifier
     * @param plugin              the plugin instance
     * @param fileName            the configuration file name inside the {@code menus} folder
     * @param title               the default menu title
     * @param isTitleTranslated   whether the title should be localized
     * @param size                the number of rows, from 1 to 6
     * @param defaultDynamicSlots the default dynamic slot definitions
     * @param defaultButtons      the default button definitions
     */
    @SneakyThrows
    public GuiBase(String id, IYggraPlugin plugin, String fileName, String title, boolean isTitleTranslated, int size, HashMap<String, List<String>> defaultDynamicSlots,
                   HashSet<GuiButton> defaultButtons) {
        yaml = new YamlConfiguration();
        Path filePath = Paths.get(plugin.dataFolder().getPath(), "menus", fileName);
        file = filePath.toFile();
        try {
            if (!file.exists()) {

                if (!file.isDirectory()) {
                    //noinspection ResultOfMethodCallIgnored
                    file.getParentFile().mkdirs();
                }
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            }
            else {
                yaml.load(file);
            }
        }
        catch (Exception ignored) {}

        this.id = id;
        this.plugin = plugin;
        this.logger = plugin.logger().withModule(GuiBase.class);
        this.translator = plugin.getRequiredService(ITranslationService.class);
        this.guiRenderer = plugin.getRequiredService(IGuiRenderer.class);
        this.itemStackAccessor = plugin.getRequiredService(IItemStackAccessor.class);
        this.itemSerializer = plugin.getRequiredService(IItemSerializer.class);

        this.menuTitle = yaml.getString("title", title);
        this.isTitleTranslated = yaml.getBoolean("title-translated", isTitleTranslated);
        if (size > 6 || size < 1)
            size = 6;
        this.menuSize = yaml.getInt("size", size);
        this.dynamicSlots = resolveDynamicSlots(defaultDynamicSlots);
        this.buttons = resolveButtons(defaultButtons);
        try {
            yaml.save(file);
        }
        catch (Exception ignored) { }
    }

    /**
     * Creates and returns the BaseGui for the given player, applying localized title and button layouts.
     *
     * @param player The player to create the menu for.
     * @return The constructed BaseGui instance.
     */
    public BaseGui create(Player player) {
        String rawTitle = isTitleTranslated ? translator.localize(player.getUniqueId(), menuTitle) : menuTitle;
        Component titleComponent = AdventureUtils.parse(rawTitle, true);

        BaseGui gui = Gui.gui()
                .title(titleComponent)
                .rows(menuSize)
                .create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        for (GuiButton button : buttons) {
            button.apply(player, translator, itemStackAccessor, guiRenderer, gui, this);
        }
        return gui;
    }

    /**
     * Refreshes the given menu for the specified player.
     *
     * @param player The player viewing the menu.
     * @param menu   The menu to refresh.
     */
    public abstract void refresh(Player player, BaseGui menu);

    /**
     * Executes a button action for the given player.
     *
     * @param player The player who triggered the action.
     * @param action The action identifier.
     * @param event  The inventory click event.
     */
    public abstract void executeCommand(Player player, String action, InventoryClickEvent event);

    /**
     * Called when a player opens this menu. Override to implement custom behavior.
     *
     * @param player The player who opened the menu.
     * @param args Optional contextual arguments
     */
    @SuppressWarnings("unused")
    public void onOpen(Player player, Object... args) {
        // optional
    }

    /**
     * Called when a player closes this menu. Override to implement custom behavior.
     *
     * @param player The player who closed the menu.
     */
    @SuppressWarnings("unused")
    public void onClose(Player player) {
        // optional
    }
    private HashMap<String, List<Integer>> resolveDynamicSlots(HashMap<String, List<String>> defaultDynamicSlots) {
        HashMap<String, List<Integer>> dynamicSlots = new HashMap<>();

        ConfigurationSection section = yaml.getConfigurationSection("dynamic-slots");
        if (section == null || section.getKeys(false).isEmpty()) {
            yaml.set("dynamic-slots", defaultDynamicSlots);
            for (String key : defaultDynamicSlots.keySet()) {
                dynamicSlots.put(key, SlotParser.parse(defaultDynamicSlots.get(key)));
            }
            return dynamicSlots;
        }
        for (String key : section.getKeys(false)) {
            List<String> rawSlotList = yaml.getStringList("dynamic-slots." + key);
            dynamicSlots.put(key, SlotParser.parse(rawSlotList));
        }
        return dynamicSlots;
    }

    /**
     * Resolves buttons from the configuration, falling back to defaults if no config entry exists.
     *
     * @param defaultButtons The default set of buttons.
     * @return The resolved set of GuiButtons.
     */
    private HashSet<GuiButton> resolveButtons(HashSet<GuiButton> defaultButtons) {
        HashSet<GuiButton> buttons = new LinkedHashSet<>();

        List<Map<?, ?>> buttonMap = yaml.getMapList("buttons");
        if (buttonMap.isEmpty()) {
            List<Map<String, Object>> defaultList = new ArrayList<>();
            for (GuiButton button : defaultButtons) {
                defaultList.add(button.toMap(itemSerializer));
            }
            yaml.set("buttons", defaultList);
            return defaultButtons;
        }
        for (Map<?, ?> btnElem : buttonMap) {
            GuiButton btn = GuiButton.fromMap(btnElem, itemSerializer, logger);
            if (btn != null) {
                buttons.add(btn);
            }
            else {
                logger.warn("Failed to resolve: " + btnElem);
            }
        }
        return buttons;
    }
}