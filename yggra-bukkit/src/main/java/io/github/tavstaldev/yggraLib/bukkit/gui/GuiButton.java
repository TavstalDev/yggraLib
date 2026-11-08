package io.github.tavstaldev.yggraLib.bukkit.gui;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import io.github.tavstaldev.yggraLib.api.gui.SlotParser;
import io.github.tavstaldev.yggraLib.api.localization.ITranslationService;
import io.github.tavstaldev.yggraLib.api.logger.YggraLogger;
import io.github.tavstaldev.yggraLib.api.utils.TypeUtils;
import io.github.tavstaldev.yggraLib.bukkit.items.ItemSerializerBase;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a single button in a GUI menu, with support for materials, head textures,
 * titles, lore, slot positioning, and click actions.
 */
public class GuiButton {
    private final @Nullable String action;
    private @Nullable final String title;
    private final boolean translateTitle;
    private @Nullable final List<String> lore;
    private final boolean translateLore;
    private final ItemStack displayItem;
    private final List<String> slots;
    private List<Integer> slotCache = null;

    /**
     * Constructs a new GuiButton.
     *
     * @param action         The action identifier for click handling.
     * @param title          Optional display title.
     * @param translateTitle Whether the title should be localized.
     * @param lore           Optional lore lines.
     * @param translateLore  Whether the lore should be localized.
     * @param slots          The slot definitions (e.g. "0-4", "5").
     */
    public GuiButton(@Nullable String action,
            @Nullable String title,
            boolean translateTitle,
            @Nullable List<String> lore,
            boolean translateLore,
            List<String> slots,
            ItemStack displayItem) {
        this.action = action;
        this.title = title;
        this.translateTitle = translateTitle;
        this.lore = lore;
        this.translateLore = translateLore;
        this.slots = slots;
        this.displayItem = displayItem;
    }

    /** @return The action of the button. */
    public @Nullable String getAction() {return action;}

    public ItemStack getDisplayItem() {
        return displayItem.clone();
    }
    /** @return The optional display title. */
    public @Nullable String getTitle() { return title; }
    /** @return Whether the title should be localized. */
    public boolean shouldTranslateTitle() { return translateTitle; }
    /** @return The optional lore lines. */
    public @Nullable List<String> getLore() { return lore; }
    /** @return Whether the lore should be localized. */
    public boolean shouldTranslateLore() { return  translateLore; }
    /** @return The raw slot definitions. */
    public List<String> getSlotList() { return slots; }

    /**
     * Returns the parsed slot numbers, caching the result.
     *
     * @return The list of resolved slot indices.
     */
    public List<Integer> getSlots() {
        if (slotCache != null)
            return slotCache;
        slotCache = SlotParser.parse(slots);
        return slotCache;
    }

    /**
     * Applies this button to the given menu at its resolved slots, attaching the click listener.
     *
     * @param player     The player viewing the menu.
     * @param translator The translation service.
     * @param baseGui    The menu to place the button in.
     * @param gui        The GUI base for action execution.
     */
    public void apply(@NotNull final Player player, @NotNull final ITranslationService translator,
                      @NotNull final IGuiRenderer guiRenderer,
                      @NotNull BaseGui baseGui, @NotNull GuiBase gui) {
        List<Integer> slots = getSlots();
        if (slots.isEmpty())
            return;

        ItemStack itemStack = guiRenderer.render(this, player, translator);

        GuiItem btn = new GuiItem(itemStack, event -> {
            if (action == null)
                return;
            gui.executeCommand(player, action, event);
        });
        for (Integer slot : slots) {
            baseGui.setItem(slot, btn);
        }
    }

    /**
     * Serializes this button into a configuration-friendly map.
     *
     * @return The map representation of this button.
     */
    public Map<String, Object> toMap(ItemSerializerBase itemSerializer) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (action != null) {
            map.put("action", action);
        }
        if (title != null) {
            map.put("title", title);
        }
        map.put("translate-title", translateTitle);
        if (lore != null) {
            map.put("lore", lore);
        }
        map.put("translate-lore", translateLore);
        if (slots != null) {
            if (slots.size() == 1) {
                String slotValue = slots.get(0);
                if (slotValue.contains("-"))
                    map.put("slots", slots);
                else
                    map.put("slot", Integer.parseInt(slotValue));
            } else {
                map.put("slots", slots);
            }
        }
        map.put("item", itemSerializer.serializeItemStack(displayItem));
        return map;
    }

    /**
     * Deserializes a GuiButton from a configuration map.
     *
     * @param map The map containing button data.
     * @return The deserialized GuiButton, or null on failure.
     */
    public static @Nullable GuiButton fromMap(Map<?, ?> map, ItemSerializerBase itemSerializer, @Nullable YggraLogger logger) {
        if (map == null) return null;

        try {
            String action = map.containsKey("action") ? (String) map.get("action") : null;
            String title = (String) map.get("title");
            boolean translateTitle = (Boolean)map.get("translate-title");
            boolean translateLore = (Boolean)map.get("translate-lore");
            Map<String, Object> itemMap = TypeUtils.castAsMap(map.get("item"), null);
            if (itemMap == null) {
                if (logger != null) {
                    logger.error("Failed to get item map for gui button.");
                }
                return null;
            }

            ItemStack displayItem = itemSerializer.deserializeItemStack(itemMap);
            List<String> lore = null;
            if (map.containsKey("lore") && map.get("lore") instanceof List<?>) {
                lore = ((List<?>)map.get("lore")).stream().map(Object::toString).collect(Collectors.toList());
            }

            List<String> slots = null;
            if (map.containsKey("slots") && map.get("slots") instanceof List<?>)
            {
                slots = ((List<?>)map.get("slots")).stream().map(Object::toString).collect(Collectors.toList());
            }

            if (slots == null) {
                Integer slot = (Integer) map.get("slot");
                slots = new ArrayList<>();
                if (slot != null)
                    slots.add(slot.toString());
            }

            return new GuiButton(action, title, translateTitle, lore, translateLore, slots, displayItem);
        } catch (Exception ex) {
            if (logger != null) {
                logger.error("Failed to get guiButton from map.", ex);
            }
            return null;
        }
    }
}