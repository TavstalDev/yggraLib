package io.github.tavstaldev.yggra.core.gui;

import com.samjakob.spigui.menu.SGMenu;
import io.github.tavstaldev.yggra.core.YggraPlugin;
import io.github.tavstaldev.yggra.core.configuration.YggraConfiguration;
import io.github.tavstaldev.yggra.core.logger.YggraLogger;
import io.github.tavstaldev.yggra.core.services.TranslationService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

public abstract class GuiBase extends YggraConfiguration {
    protected final YggraPlugin plugin;
    protected final YggraLogger logger;
    protected final TranslationService translator;
    public final String id;
    protected String menuTitle;
    protected boolean isTitleTranslated;
    protected int menuSize;
    protected HashMap<String, List<Integer>> dynamicSlots;
    protected HashSet<GuiButton> buttons;

    public GuiBase(String id, YggraPlugin plugin, String fileName, String title, boolean isTitleTranslated, int size, HashMap<String, List<String>> defaultDynamicSlots,
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
    }

    @Override
    public final void loadDefaults() {
        // Prevent overwriting of defaults, as they are already set in the constructor
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

    protected HashSet<GuiButton> resolveButtons(HashSet<GuiButton> defaultButtons) {
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

    public abstract SGMenu create(Player player);

    public abstract void refresh(Player player, SGMenu menu);

    public abstract void executeCommand(Player player, String action, InventoryClickEvent event);

    public void onOpen(Player player) {
        // optional
    }

    public void onClose(Player player) {
        // optional
    }
}