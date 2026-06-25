package io.github.tavstaldev.yggra.core.gui;

import com.samjakob.spigui.menu.SGMenu;
import io.github.tavstaldev.yggra.core.YggraPlugin;
import io.github.tavstaldev.yggra.core.configuration.YggraConfiguration;
import io.github.tavstaldev.yggra.core.logger.YggraLogger;
import io.github.tavstaldev.yggra.core.services.TranslationService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class GuiBase extends YggraConfiguration {
    protected final YggraPlugin plugin;
    protected final YggraLogger logger;
    protected final TranslationService translator;
    protected String menuTitle;
    protected boolean isMenuTitleTranslated;
    protected int menuSize;
    protected HashMap<String, List<Integer>> dynamicSlots;
    protected HashSet<GuiButton> menuButtons;

    public GuiBase(YggraPlugin plugin, String fileName) {
        super(plugin, "menus/" + fileName, null);
        this.plugin = plugin;
        this.logger = plugin.logger().withModule(GuiBase.class);
        this.translator = plugin.translator();
    }

    protected HashMap<String, List<Integer>> resolveDynamicSlots(HashMap<String, List<String>> defaultDynamicSlots) {
        HashMap<String, List<Integer>> dynamicSlots = new HashMap<>();

        ConfigurationSection section = this.getConfigurationSection("dynamicSlots");
        if (section == null || section.getKeys(false).isEmpty()) {
            this.set("dynamicSlots", defaultDynamicSlots);
            for (String key : defaultDynamicSlots.keySet()) {
                dynamicSlots.put(key, GuiUtils.resolveSlots(defaultDynamicSlots.get(key)));
            }
            return dynamicSlots;
        }
        for (String key : section.getKeys(false)) {
            List<String> rawSlotList = this.getStringList("dynamicSlots." + key);
            dynamicSlots.put(key, GuiUtils.resolveSlots(rawSlotList));
        }
        return dynamicSlots;
    }

    protected HashSet<GuiButton> resolveButtons(LinkedHashSet<GuiButton> defaultButtons) {
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
        for (Map<?, ?> actionMap : buttonMap) {
            GuiButton action = GuiButton.fromMap(actionMap);
            if (action != null) {
                buttons.add(action);
            }
        }
        return buttons;
    }

    public abstract SGMenu create(Player player);

    public abstract void refresh(Player player, SGMenu menu);

    public abstract void executeCommand(Player player, String command);

    public void onOpen(Player player) {
        // optional
    }

    public void onClose(Player player) {
        // optional
    }
}