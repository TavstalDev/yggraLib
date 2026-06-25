package io.github.tavstaldev.yggra.core.services;

import io.github.tavstaldev.yggra.core.YggraPlugin;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Map;

/**
 * Sends {@link Title} messages to players with localized text and predefined timings.
 */
public class TitleService {
    /** The plugin instance, used for translation lookups. */
    private final YggraPlugin plugin;
    /** Used to translate color codes in title/subtitle text. */
    private final ChatService chat;

    /**
     * @param plugin The plugin instance, used for translation lookups.
     * @param chat   Used to translate color codes in title/subtitle text.
     */
    public TitleService(YggraPlugin plugin, ChatService chat) {
        this.plugin = plugin;
        this.chat = chat;
    }

    /**
     * Sends a localized title to a player with custom timing.
     *
     * @param player    the target player
     * @param key       the translation key (".title" and ".subtitle" suffixes are appended)
     * @param arguments arguments for translation placeholders
     * @param times     the title timing (fade-in, stay, fade-out)
     */
    public void send(Player player, String key, Map<String, Object> arguments, Title.Times times) {
        if (arguments == null) {
            arguments = Map.of();
        }

        var translator = plugin.translator();
        String mainTitle = translator.localize(player, key + ".title", arguments);
        String subTitle = translator.localize(player, key + ".subtitle", arguments);
        if (mainTitle.isEmpty() && subTitle.isEmpty()) {
            return;
        }

        Title title = Title.title(chat.translateColors(mainTitle, true), chat.translateColors(subTitle, true), times);
        player.showTitle(title);
    }

    /**
     * Sends a localized title with default timing (0.5s fade-in, 2s stay, 1s fade-out).
     *
     * @param player    the target player
     * @param key       the translation key (".Title" and ".Subtitle" suffixes are appended)
     * @param arguments arguments for translation placeholders
     */
    public void send(Player player, String key, Map<String, Object> arguments) {
        Title.Times times = Title.Times.times(
                Duration.ofMillis(500),   // Fade In: 0.5 seconds
                Duration.ofSeconds(2),    // Stay: 2 seconds
                Duration.ofSeconds(1)     // Fade Out: 1 second
        );
        send(player, key, arguments, times);
    }

    /**
     * Sends a raw title with custom timing.
     *
     * @param player   the target player
     * @param title    the main title text (may contain color codes)
     * @param subTitle the subtitle text (may contain color codes)
     * @param times    the title timing (fade-in, stay, fade-out)
     */
    public void send(Player player, String title, String subTitle, Title.Times times) {
        if (title.isEmpty() && subTitle.isEmpty()) {
            return;
        }

        Title screenTitle = Title.title(chat.translateColors(title, true), chat.translateColors(subTitle, true), times);
        player.showTitle(screenTitle);
    }

    /**
     * Sends a raw title with default timing (0.5s fade-in, 2s stay, 1s fade-out).
     *
     * @param player   the target player
     * @param title    the main title text (may contain color codes)
     * @param subTitle the subtitle text (may contain color codes)
     */
    public void send(Player player, String title, String subTitle) {
        Title.Times times = Title.Times.times(
                Duration.ofMillis(500),   // Fade In: 0.5 seconds
                Duration.ofSeconds(2),    // Stay: 2 seconds
                Duration.ofSeconds(1)     // Fade Out: 1 second
        );
        send(player, title, subTitle, times);
    }
}
