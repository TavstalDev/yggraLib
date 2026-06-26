package io.github.tavstaldev.yggra.core.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import io.github.tavstaldev.yggra.core.gui.renderer.GuiItemRenderer;
import io.github.tavstaldev.yggra.core.services.TranslationService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a single button in a GUI menu, with support for materials, head textures,
 * titles, lore, slot positioning, and click actions.
 */
public class GuiButton {
    private final @Nullable String action;
    private final Material material;
    private @Nullable final String headTexture;
    private final Integer amount;
    private @Nullable final String title;
    private final boolean translateTitle;
    private @Nullable final List<String> lore;
    private final boolean translateLore;
    private final List<String> slots;
    private List<Integer> slotCache = null;

    /**
     * Constructs a new GuiButton.
     *
     * @param action         The action identifier for click handling.
     * @param material       The material of the button.
     * @param headTexture    Optional base64 head texture.
     * @param amount         The item amount.
     * @param title          Optional display title.
     * @param translateTitle Whether the title should be localized.
     * @param lore           Optional lore lines.
     * @param translateLore  Whether the lore should be localized.
     * @param slots          The slot definitions (e.g. "0-4", "5").
     */
    public GuiButton(@Nullable String action,
            Material material,
            @Nullable String headTexture,
            Integer amount,
            @Nullable String title,
            boolean translateTitle,
            @Nullable List<String> lore,
            boolean translateLore,
            List<String> slots) {
        this.action = action;
        this.material = material;
        this.headTexture = headTexture;
        this.amount = amount;
        this.title = title;
        this.translateTitle = translateTitle;
        this.lore = lore;
        this.translateLore = translateLore;
        this.slots = slots;
    }

    /** @return The material of the button. */
    public Material getMaterial() {return material; }
    /** @return The optional base64 head texture. */
    public @Nullable String getHeadTexture() { return headTexture; }
    /** @return The item amount. */
    public Integer getAmount() { return  amount; }
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
     * Renders this button into an SGButton for the given player.
     *
     * @param player     The player viewing the button.
     * @param translator The translation service.
     * @return The constructed SGButton.
     */
    public SGButton get(final Player player, final TranslationService translator) {
        ItemStack itemStack = GuiItemRenderer.render(this, player, translator);
        return new SGButton(itemStack);
    }

    /**
     * Applies this button to the given menu at its resolved slots, attaching the click listener.
     *
     * @param player     The player viewing the menu.
     * @param translator The translation service.
     * @param sgMenu     The menu to place the button in.
     * @param gui        The GUI base for action execution.
     */
    public void apply(@NotNull final Player player, @NotNull final TranslationService translator, @NotNull SGMenu sgMenu, @NotNull GuiBase gui) {
        List<Integer> slots = getSlots();
        if (slots.isEmpty())
            return;

        SGButton btn = get(player, translator);
        if (action != null) {
            btn.withListener(x -> {
                gui.executeCommand(player, action, x);
            });
        }
        for (Integer slot : slots) {
            sgMenu.setButton(0, slot, btn);
        }
    }

    /**
     * Serializes this button into a configuration-friendly map.
     *
     * @return The map representation of this button.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (action != null) {
            map.put("action", action);
        }
        if (headTexture != null) {
            map.put("head", headTexture);
        } else {
            map.put("material", material.toString());
        }
        map.put("amount", amount);
        if (title != null) {
            map.put("title", title);
        }
        if (lore != null) {
            map.put("lore", lore);
        }
        if (slots != null) {
            if (slots.size() == 1) {
                var slotValue = slots.getFirst();
                if (slotValue.contains("-"))
                    map.put("slots", slots);
                else
                    map.put("slot", Integer.parseInt(slotValue));
            } else {
                map.put("slots", slots);
            }
        }
        return map;
    }

    /**
     * Deserializes a GuiButton from a configuration map.
     *
     * @param map The map containing button data.
     * @return The deserialized GuiButton, or null on failure.
     */
    public static @Nullable GuiButton fromMap(Map<?, ?> map) {
        if (map == null) return null;

        try {
            String action = map.containsKey("action") ? (String) map.get("action") : null;
            Material material = map.containsKey("head") ? Material.PLAYER_HEAD : Material.getMaterial(((String) map.get("material")).toUpperCase());
            String headTexture = map.containsKey("head") ? (String) map.get("head") : null;
            Integer amount = (Integer) map.get("amount");
            String title = (String) map.get("title");
            boolean translateTitle = (boolean)map.get("translate-title");
            boolean translateLore = (boolean)map.get("translate-lore");

            List<String> lore = map.containsKey("lore") && map.get("lore") instanceof List<?> loreList
                    ? loreList.stream().map(Object::toString).toList()
                    : null;

            List<String> slots = map.containsKey("slots") && map.get("slots") instanceof List<?> slotsList
                    ? slotsList.stream().map(Object::toString).toList()
                    : null;
            if (slots == null) {
                Integer slot = (Integer) map.get("slot");
                slots = slot != null ? List.of(slot.toString()) : new ArrayList<>();
            }

            return new GuiButton(action, material, headTexture, amount, title, translateTitle, lore, translateLore, slots);
        } catch (Exception ex) {
            return null;
        }
    }
}