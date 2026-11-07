package io.github.tavstaldev.yggra.impl.v1_8_8.services;

import io.github.tavstaldev.yggra.api.IYggraPlugin;
import io.github.tavstaldev.yggra.api.localization.ITranslationService;
import io.github.tavstaldev.yggra.bukkit.services.IActionBarService;
import io.github.tavstaldev.yggra.bukkit.utils.AdventureUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class ActionBarService_1_8_8 implements IActionBarService {
    private final IYggraPlugin plugin;
    private final BukkitAudiences adventure;
    private final ITranslationService translationService;

    public ActionBarService_1_8_8(IYggraPlugin plugin, ITranslationService translationService) {
        this.plugin = plugin;
        this.translationService = translationService;
        this.adventure = BukkitAudiences.create((JavaPlugin)plugin);
    }

    @Override
    public void sendRichMsg(@NotNull Player player, @NotNull String message) {
        String msg = plugin.replacePlaceholders(message);
        adventure.player(player).sendActionBar(AdventureUtils.parse(msg, true));
    }

    @Override
    public void sendRichMsg(@NotNull Player player, @NotNull String message, @NotNull Map<String, Object> parameters) {
        String msg = message;

        // Get the keys
        Set<String> keys = parameters.keySet();
        for (@RegExp String dirKey : keys) {
            @RegExp String finalKey;
            if (dirKey.startsWith("%"))
                finalKey = dirKey;
            else
                finalKey = "%" + dirKey + "%";
            msg = msg.replace(finalKey, parameters.get(dirKey).toString());
        }

        msg = plugin.replacePlaceholders(msg);
        adventure.player(player).sendActionBar(AdventureUtils.parse(msg, true));
    }

    @Override
    public void sendLocalizedMsg(@NotNull Player player, @NotNull String key) {
        String msg = translationService.localize(player.getUniqueId(), key);
        msg = plugin.replacePlaceholders(msg);
        adventure.player(player).sendActionBar(AdventureUtils.parse(msg, true));
    }

    @Override
    public void sendLocalizedMsg(@NotNull Player player, @NotNull String key, @NotNull Map<String, Object> parameters) {
        String msg = translationService.localize(player.getUniqueId(), key, parameters);
        msg = plugin.replacePlaceholders(msg);
        adventure.player(player).sendActionBar(AdventureUtils.parse(msg, true));
    }
}
