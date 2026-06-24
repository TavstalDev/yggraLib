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
     * Sends a title and subtitle to the specified player using localized strings.
     * The title and subtitle are fetched using the provided localization key and arguments.
     * If both the title and subtitle are empty, no title is sent.
     *
     * @param player    The player to whom the title will be sent.
     * @param key       The localization key used to fetch the title and subtitle.
     * @param arguments A map of arguments to be used in the localization process. Can be null.
     */
    public void send(Player player, String key, Map<String, Object> arguments) {
        if (arguments == null) {
            arguments = Map.of();
        }

        var translator = plugin.translator();
        String mainTitle = translator.localize(player, key + ".Title", arguments);
        String subTitle = translator.localize(player, key + ".Subtitle", arguments);
        if (mainTitle.isEmpty() && subTitle.isEmpty()) {
            return;
        }

        Title.Times times = Title.Times.times(
                Duration.ofMillis(500),   // Fade In: 0.5 seconds
                Duration.ofSeconds(2),    // Stay: 2 seconds
                Duration.ofSeconds(1)     // Fade Out: 1 second
        );
        Title title = Title.title(chat.translateColors(mainTitle, true), chat.translateColors(subTitle, true), times);
        player.showTitle(title);
    }

    /**
     * Sends a title and subtitle to the specified player.
     * The title and subtitle are displayed with predefined fade-in, stay, and fade-out durations.
     * If both the title and subtitle are empty, no title is sent.
     *
     * @param player   The player to whom the title will be sent.
     * @param title    The main title text to display. Can be empty.
     * @param subTitle The subtitle text to display. Can be empty.
     */
    public void send(Player player, String title, String subTitle) {
        if (title.isEmpty() && subTitle.isEmpty()) {
            return;
        }

        Title.Times times = Title.Times.times(
                Duration.ofMillis(500),   // Fade In: 0.5 seconds
                Duration.ofSeconds(2),    // Stay: 2 seconds
                Duration.ofSeconds(1)     // Fade Out: 1 second
        );
        Title screenTitle = Title.title(chat.translateColors(title, true), chat.translateColors(subTitle, true), times);
        player.showTitle(screenTitle);
    }
}
