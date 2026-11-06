package io.github.tavstaldev.yggra.impl.v1_21_5.services;

import io.github.tavstaldev.yggra.api.IYggraPlugin;
import io.github.tavstaldev.yggra.api.localization.ITranslationService;
import io.github.tavstaldev.yggra.bukkit.services.ITitleService;
import io.github.tavstaldev.yggra.bukkit.utils.AdventureUtils;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Map;

public class TitleService_1_21_5 implements ITitleService {
    private final IYggraPlugin plugin;
    private final ITranslationService translationService;

    public TitleService_1_21_5(IYggraPlugin plugin, ITranslationService translationService) {
        this.plugin = plugin;
        this.translationService = translationService;
    }

    public void send(Player player, String key, Map<String, Object> arguments, Title.Times times) {
        if (arguments == null) {
            arguments = Map.of();
        }

        String mainTitle = translationService.localize(player.getUniqueId(), key + ".title", arguments);
        String subTitle = translationService.localize(player.getUniqueId(), key + ".subtitle", arguments);
        if (mainTitle.isEmpty() && subTitle.isEmpty()) {
            return;
        }

        Title title = Title.title(AdventureUtils.parse(mainTitle, true),
        AdventureUtils.parse(subTitle, true), times);
        player.showTitle(title);
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
        player.showTitle(screenTitle);
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
