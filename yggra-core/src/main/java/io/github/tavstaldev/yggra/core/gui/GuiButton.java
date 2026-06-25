package io.github.tavstaldev.yggra.core.gui;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import io.github.tavstaldev.yggra.core.services.ChatService;
import io.github.tavstaldev.yggra.core.services.TranslationService;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GuiButton {
    private final Material material;
    private @Nullable final String headTexture;
    private final Integer amount;
    private @Nullable final String title;
    private final boolean translateTitle;
    private @Nullable final List<String> lore;
    private final boolean translateLore;
    private @Nullable final Integer slot;
    private @Nullable final List<String> slots;
    private @Nullable final List<String> commands;

    private List<Integer> slotCache = null;

    public GuiButton(Material material,
                      @Nullable String headTexture,
                      Integer amount,
                      @Nullable String title,
                      boolean translateTitle,
                      @Nullable List<String> lore,
                      boolean translateLore,
                      @Nullable Integer slot,
                      @Nullable List<String> slots,
                      @Nullable List<String> commands) {
        this.material = material;
        this.headTexture = headTexture;
        this.amount = amount;
        this.title = title;
        this.translateTitle = translateTitle;
        this.lore = lore;
        this.translateLore = translateLore;
        this.slot = slot;
        this.slots = slots;
        this.commands = commands;
    }

    public Material getMaterial() {return material; }
    public @Nullable String getHeadTexture() { return headTexture; }
    public Integer getAmount() { return  amount; }
    public @Nullable String getTitle() { return title; }
    public boolean shouldTranslateTitle() { return translateTitle; }
    public @Nullable List<String> getLore() { return lore; }
    public boolean shouldTranslateLore() { return  translateLore; }
    public @Nullable Integer getSlot() { return slot; }
    public @Nullable List<String> getSlotList() { return slots; }
    public @Nullable List<String> getCommands() { return commands; }



    public SGButton get(Player player, ChatService chat, TranslationService translator) {
        ItemStack itemStack = toItemStack(player,chat, translator);
        return new SGButton(itemStack);
    }

    public List<Integer> getSlots() {
        if (slots != null) {
            if (slotCache != null)
                return slotCache;
            slotCache = GuiUtils.resolveSlots(slots);
            return slotCache;
        }
        else if (slot != null) {
            return List.of(slot);
        }
        else {
            return new ArrayList<>();
        }
    }

    public void apply(@NotNull final Player player, @NotNull ChatService chat, @NotNull final TranslationService translator, @NotNull SGMenu sgMenu, @NotNull GuiBase menu) {
        List<Integer> slots = getSlots();
        if (slots.isEmpty())
            return;

        String playerName = player.getName();
        List<String> commands = getCommands();
        SGButton btn = get(player, chat, translator);
        if (commands != null && !commands.isEmpty()) {
            btn = btn.withListener((InventoryClickEvent event) -> {
                for (String cmd : commands) {
                    menu.executeCommand(player, cmd.replace("%player%", playerName));
                }
            });
        }
        for (Integer slot : slots) {
            sgMenu.setButton(0, slot, btn);
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (headTexture != null) {
            map.put("head", headTexture);
        } else {
            map.put("material", material.toString());
        }
        map.put("amount", amount);
        if (title != null) {
            map.put("title", title);
        }
        if (titleKey != null) {
            map.put("titleKey", titleKey);
        }
        if (loreKey != null) {
            map.put("loreKey", loreKey);
        }
        if (lore != null) {
            map.put("lore", lore);
        }
        if (slot != null) {
            map.put("slot", slot);
        }
        if (slots != null) {
            map.put("slots", slots);
        }
        if (commands != null) {
            map.put("commands", commands);
        }
        return map;
    }

    public static @Nullable GuiButton fromMap(Map<?, ?> map) {
        if (map == null) return null;

        try {
            Material material = map.containsKey("head") ? Material.PLAYER_HEAD : Material.getMaterial(((String) map.get("material")).toUpperCase());
            String headTexture = map.containsKey("head") ? (String) map.get("head") : null;
            Integer amount = (Integer) map.get("amount");
            String title = (String) map.get("title");
            String titleKey = (String) map.get("titleKey");
            String loreKey = (String) map.get("loreKey");

            List<String> lore = map.containsKey("lore") && map.get("lore") instanceof List<?> loreList
                    ? loreList.stream().map(Object::toString).toList()
                    : null;

            Integer slot = (Integer) map.get("slot");
            List<String> slots = map.containsKey("slots") && map.get("slots") instanceof List<?> slotsList
                    ? slotsList.stream().map(Object::toString).toList()
                    : null;

            List<String> commands = map.containsKey("commands") && map.get("commands") instanceof List<?> commandList
                    ? commandList.stream().map(Object::toString).toList()
                    : null;

            return new GuiButton(material, headTexture, amount, title, titleKey, loreKey, lore, slot, slots, commands);
        } catch (Exception ex) {
            return null;
        }
    }
}
