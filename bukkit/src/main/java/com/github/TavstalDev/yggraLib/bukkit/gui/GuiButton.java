package com.github.TavstalDev.yggraLib.bukkit.gui;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import com.github.TavstalDev.yggraLib.api.gui.SlotParser;
import com.github.TavstalDev.yggraLib.api.localization.ITranslationService;
import com.github.TavstalDev.yggraLib.api.logger.YggraLogger;
import com.github.TavstalDev.yggraLib.api.utils.TypeUtils;
import com.github.TavstalDev.yggraLib.bukkit.items.IItemStackAccessor;
import com.github.TavstalDev.yggraLib.bukkit.items.ItemSerializerBase;
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
     * Creates a new GUI button.
     *
     * @param action         the action identifier used for click handling
     * @param title          the optional display title
     * @param translateTitle whether the title should be localized
     * @param lore           the optional lore lines
     * @param translateLore  whether the lore should be localized
     * @param slots          the slot definitions, for example {@code "0-4"} or {@code "5"}
     * @param displayItem    the item shown for this button
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

    /**
     * Gets the action identifier for this button.
     *
     * @return the action identifier, or {@code null} if none is set
     */
    public @Nullable String getAction() {return action;}

    /**
     * Gets a copy of the display item for this button.
     *
     * @return a cloned display item
     */
    public ItemStack getDisplayItem() {
        return displayItem.clone();
    }

    /**
     * Gets the configured title for this button.
     *
     * @return the title, or {@code null} if none is set
     */
    public @Nullable String getTitle() { return title; }

    /**
     * Checks whether the title should be localized.
     *
     * @return {@code true} if the title should be translated
     */
    public boolean shouldTranslateTitle() { return translateTitle; }

    /**
     * Gets the configured lore lines for this button.
     *
     * @return the lore lines, or {@code null} if none are set
     */
    public @Nullable List<String> getLore() { return lore; }

    /**
     * Checks whether the lore should be localized.
     *
     * @return {@code true} if the lore should be translated
     */
    public boolean shouldTranslateLore() { return  translateLore; }

    /**
     * Gets the raw slot definitions from configuration.
     *
     * @return the raw slot definitions
     */
    public List<String> getSlotList() { return slots; }

    /**
     * Gets the parsed slot indices for this button.
     *
     * @return the resolved slot indices
     */
    public List<Integer> getSlots() {
        if (slotCache != null)
            return slotCache;
        slotCache = SlotParser.parse(slots);
        return slotCache;
    }

    /**
     * Applies this button to the given menu at its resolved slots.
     *
     * @param player            the player viewing the menu
     * @param translator        the translation service
     * @param itemStackAccessor the item stack accessor
     * @param guiRenderer       the renderer used to create the display item
     * @param baseGui           the menu to place the button in
     * @param gui               the parent GUI used for action handling
     */
    public void apply(@NotNull final Player player, @NotNull final ITranslationService translator,
                      @NotNull final IItemStackAccessor itemStackAccessor, @NotNull final IGuiRenderer guiRenderer,
                      @NotNull BaseGui baseGui, @NotNull GuiBase gui) {
        List<Integer> slots = getSlots();
        if (slots.isEmpty())
            return;

        ItemStack itemStack = guiRenderer.render(this, player, translator, itemStackAccessor);

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
     * @param itemSerializer the serializer used for the display item
     * @return the map representation of this button
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
     * Creates a {@code GuiButton} from a configuration map.
     *
     * @param map            the map containing button data
     * @param itemSerializer the serializer used for the display item
     * @param logger         the logger used for error messages, or {@code null} to disable logging
     * @return the created button, or {@code null} if deserialization fails
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