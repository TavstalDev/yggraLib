package io.github.tavstaldev.yggra.core.gui.renderer;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.github.tavstaldev.yggra.core.gui.GuiButton;
import io.github.tavstaldev.yggra.core.gui.GuiDupeDetector;
import io.github.tavstaldev.yggra.core.services.ChatService;
import io.github.tavstaldev.yggra.core.services.TranslationService;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class GuiItemRenderer {

    public ItemStack render(GuiButton button,
                            Player player,
                            ChatService chat,
                            TranslationService translator) {

        ItemStack itemStack = new ItemStack(button.getMaterial(), button.getAmount());
        ItemMeta meta = itemStack.getItemMeta();
        String buttonTitle = button.getTitle();
        if (buttonTitle != null) {
            if (button.shouldTranslateTitle()) {
                meta.displayName(chat.translateColors(translator.localize(player, buttonTitle), true));
            } else {
                meta.displayName(chat.translateColors(buttonTitle, true));
            }
        }

        List<Component> lore = new ArrayList<>();
        var buttonLore = button.getLore();
        if (buttonLore != null) {
            if (button.shouldTranslateLore() && !buttonLore.isEmpty()) {
                String loreKey = buttonLore.getFirst();
                List<String> loreLines = translator.localizeList(player, loreKey);
                for (String line : loreLines) {
                    lore.add(chat.translateColors(line, true));
                }
            } else {
                for (String line : buttonLore) {
                    if (line == null)
                        continue;
                    lore.add(chat.translateColors(line, true));
                }
            }
        }
        meta.lore(lore);

        meta.getPersistentDataContainer().set(GuiDupeDetector.getDupeProtectedKey(), PersistentDataType.BOOLEAN, true);

        var headTexture = button.getHeadTexture();
        if (headTexture != null && meta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", headTexture));
            skullMeta.setPlayerProfile(profile);
            itemStack.setItemMeta(skullMeta);
        }
        else {
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}
