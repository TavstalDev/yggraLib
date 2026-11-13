package io.github.tavstaldev.yggraLib.impl.vlegacy.services;

import io.github.tavstaldev.yggraLib.api.IYggraPlugin;
import io.github.tavstaldev.yggraLib.api.localization.ITranslationService;
import io.github.tavstaldev.yggraLib.bukkit.services.ITitleService;
import io.github.tavstaldev.yggraLib.bukkit.utils.AdventureUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class TitleService_legacy implements ITitleService {
    private final BukkitAudiences adventure;
    private final ITranslationService translationService;

    public TitleService_legacy(IYggraPlugin plugin, ITranslationService translationService) {
        this.translationService = translationService;
        this.adventure = BukkitAudiences.create((JavaPlugin)plugin);
    }

    public void send(Player player, String key, Map<String, Object> arguments, Title.Times times) {
        if (arguments == null) {
            arguments = new HashMap<>();
        }

        String mainTitle = translationService.localize(player.getUniqueId(), key + ".title", arguments);
        String subTitle = translationService.localize(player.getUniqueId(), key + ".subtitle", arguments);
        if (mainTitle.isEmpty() && subTitle.isEmpty()) {
            return;
        }

        Title title = Title.title(AdventureUtils.parse(mainTitle, true),
        AdventureUtils.parse(subTitle, true), times);
        adventure.player(player).showTitle(title);
    }

    public void send(Player player, String key, Map<String, Object> arguments) {
        Title.Times times = Title.Times.times(
                Duration.ofMillis(500),   // Fade In: 0.5 seconds
                Duration.ofSeconds(2),    // Stay: 2 seconds
                Duration.ofSeconds(1)     // Fade Out: 1 second
        );
        send(player, key, arguments, times);
    }

    public void send(Player player, String title, String subTitle, Title.Times times) {
        if (title.isEmpty() && subTitle.isEmpty()) {
            return;
        }

        Title screenTitle = Title.title(AdventureUtils.parse(title, true),
                AdventureUtils.parse(subTitle, true), times);
        adventure.player(player).showTitle(screenTitle);
    }

    public void send(Player player, String title, String subTitle) {
        Title.Times times = Title.Times.times(
                Duration.ofMillis(500),   // Fade In: 0.5 seconds
                Duration.ofSeconds(2),    // Stay: 2 seconds
                Duration.ofSeconds(1)     // Fade Out: 1 second
        );
        send(player, title, subTitle, times);
    }
}
