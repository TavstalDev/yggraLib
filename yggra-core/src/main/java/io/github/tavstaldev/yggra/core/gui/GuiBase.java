package io.github.tavstaldev.yggra.core.gui;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import io.github.tavstaldev.yggra.core.YggraPlugin;
import io.github.tavstaldev.yggra.core.configuration.YggraConfiguration;
import io.github.tavstaldev.yggra.core.logger.YggraLogger;
import io.github.tavstaldev.yggra.core.services.TranslationService;
import io.github.tavstaldev.yggra.items.api.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

/**
 * Abstract base class for all GUI menus. Handles menu creation, button resolution,
 * dynamic slot parsing, and configuration persistence.
 */
public abstract class GuiBase extends YggraConfiguration {
    protected final YggraPlugin<?> plugin;
    protected final YggraLogger logger;
    protected final TranslationService translator;
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
    public GuiBase(String id, YggraPlugin<?> plugin, String fileName, String title, boolean isTitleTranslated, int size, HashMap<String, List<String>> defaultDynamicSlots,
                   HashSet<GuiButton> defaultButtons) {
        super(plugin, "menus/" + fileName, null);
        this.id = id;
        this.plugin = plugin;
        this.logger = plugin.logger().withModule(GuiBase.class);
        this.translator = plugin.translator();

        this.menuTitle = resolveGet("title", title);
        this.isTitleTranslated = resolveGet("title-translated", isTitleTranslated);
        if (size > 6 || size < 1)
            size = 6;
        this.menuSize = resolveGet("size", size);
        this.dynamicSlots = resolveDynamicSlots(defaultDynamicSlots);
        this.buttons = resolveButtons(defaultButtons);
        this.save();
    }

    @Override
    public final void loadDefaults() {
        // Prevent overwriting of defaults, as they are already set in the constructor
    }

    /**
     * Creates and returns the BaseGui for the given player, applying localized title and button layouts.
     *
     * @param player The player to create the menu for.
     * @return The constructed BaseGui instance.
     */
    public BaseGui create(Player player) {
        GuiManager menuManager = plugin.gui();
        if (menuManager == null)
            throw new RuntimeException("Menu manager was not initialized.");
        String rawTitle = isTitleTranslated ? translator.localize(player, menuTitle) : menuTitle;
        Component titleComponent = AdventureUtils.translateColors(rawTitle, true);

        BaseGui gui = Gui.gui()
                .title(titleComponent)
                .rows(menuSize)
                .create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        for (GuiButton button : buttons) {
            button.apply(player, translator, gui, this);
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
                defaultList.add(button.toMap());
            }
            this.set("buttons", defaultList);
            return defaultButtons;
        }
        for (Map<?, ?> btnElem : buttonMap) {
            GuiButton btn = GuiButton.fromMap(btnElem);
            if (btn != null) {
                buttons.add(btn);
            }
        }
        return buttons;
    }
}