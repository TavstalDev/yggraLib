package io.github.tavstaldev.yggra.bukkit.gui;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import io.github.tavstaldev.yggra.api.IYggraPlugin;
import io.github.tavstaldev.yggra.api.gui.SlotParser;
import io.github.tavstaldev.yggra.api.localization.ITranslationService;
import io.github.tavstaldev.yggra.api.logger.YggraLogger;
import io.github.tavstaldev.yggra.bukkit.items.ItemSerializerBase;
import io.github.tavstaldev.yggra.bukkit.utils.AdventureUtils;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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
public abstract class GuiBase extends FileConfiguration {
    protected final File file;
    protected final IYggraPlugin plugin;
    protected final YggraLogger logger;
    protected final ITranslationService translator;
    protected final IGuiRenderer guiRenderer;
    protected final ItemSerializerBase itemSerializer;
    public final String id;
    protected String menuTitle;
    protected boolean isTitleTranslated;
    protected int menuSize;
    protected HashMap<String, List<Integer>> dynamicSlots;
    protected HashSet<GuiButton> buttons;

    /**
     * Constructs a new GUI menu from configuration or defaults.
     *
     * @param id                  The unique identifier for this menu.
     * @param plugin              The plugin instance.
     * @param fileName            The configuration file name (under menus/).
     * @param title               The default menu title.
     * @param isTitleTranslated   Whether the title should be localized.
     * @param size                The number of rows (1-6).
     * @param defaultDynamicSlots Default dynamic slot definitions.
     * @param defaultButtons      Default button definitions.
     */
    @SneakyThrows
    public GuiBase(String id, IYggraPlugin plugin, String fileName, String title, boolean isTitleTranslated, int size, HashMap<String, List<String>> defaultDynamicSlots,
                   HashSet<GuiButton> defaultButtons) {
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
                this.load(file);
            }
        }
        catch (Exception ignored) {}

        this.id = id;
        this.plugin = plugin;
        this.logger = plugin.logger().withModule(GuiBase.class);
        this.translator = plugin.getRequiredService(ITranslationService.class);
        this.guiRenderer = plugin.getRequiredService(IGuiRenderer.class);
        this.itemSerializer = plugin.getRequiredService(ItemSerializerBase.class);

        this.menuTitle = this.getString("title", title);
        this.isTitleTranslated = this.getBoolean("title-translated", isTitleTranslated);
        if (size > 6 || size < 1)
            size = 6;
        this.menuSize = this.getInt("size", size);
        this.dynamicSlots = resolveDynamicSlots(defaultDynamicSlots);
        this.buttons = resolveButtons(defaultButtons);
        try {
            this.save(file);
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
            button.apply(player, translator, guiRenderer, gui, this);
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
    public void onOpen(Player player, Object... args) {
        // optional
    }

    /**
     * Called when a player closes this menu. Override to implement custom behavior.
     *
     * @param player The player who closed the menu.
     */
    public void onClose(Player player) {
        // optional
    }
    private HashMap<String, List<Integer>> resolveDynamicSlots(HashMap<String, List<String>> defaultDynamicSlots) {
        HashMap<String, List<Integer>> dynamicSlots = new HashMap<>();

        ConfigurationSection section = this.getConfigurationSection("dynamic-slots");
        if (section == null || section.getKeys(false).isEmpty()) {
            this.set("dynamic-slots", defaultDynamicSlots);
            for (String key : defaultDynamicSlots.keySet()) {
                dynamicSlots.put(key, SlotParser.parse(defaultDynamicSlots.get(key)));
            }
            return dynamicSlots;
        }
        for (String key : section.getKeys(false)) {
            List<String> rawSlotList = this.getStringList("dynamic-slots." + key);
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

        List<Map<?, ?>> buttonMap = this.getMapList("buttons");
        if (buttonMap.isEmpty()) {
            List<Map<String, Object>> defaultList = new ArrayList<>();
            for (GuiButton button : defaultButtons) {
                defaultList.add(button.toMap(itemSerializer));
            }
            this.set("buttons", defaultList);
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