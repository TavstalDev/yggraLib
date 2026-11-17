package io.github.tavstaldev.yggraLib.api.localization;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Provides methods to load and read translated messages.
 */
public interface ITranslationService {

    /**
     * Reloads translation data from disk.
     *
     * @return {@code true} if the reload was successful, otherwise {@code false}
     */
    boolean refresh();

    /**
     * Gets a translated message for the default locale.
     *
     * @param key the translation key
     * @return the translated message
     */
    @NotNull String localize(@NotNull String key);

    /**
     * Gets a translated message for the default locale and replaces placeholders.
     *
     * @param key  the translation key
     * @param args the placeholder values to insert
     * @return the translated message with placeholders replaced
     */
    @NotNull String localize(@NotNull String key, @NotNull Map<String, Object> args);

    /**
     * Gets a translated message for a specific player.
     *
     * @param player the player whose locale should be used
     * @param key    the translation key
     * @return the translated message
     */
    @NotNull String localize(@NotNull UUID player, @NotNull String key);

    /**
     * Gets a translated message for a specific player and replaces placeholders.
     *
     * @param player the player whose locale should be used
     * @param key    the translation key
     * @param args   the placeholder values to insert
     * @return the translated message with placeholders replaced
     */
    @NotNull String localize(@NotNull UUID player, @NotNull String key, @NotNull Map<String, Object> args);

    /**
     * Gets a translated list of messages for the default locale.
     *
     * @param key the translation key
     * @return the translated message list
     */
    @NotNull List<String> localizeList(@NotNull String key);

    /**
     * Gets a translated list of messages for the default locale and replaces placeholders.
     *
     * @param key  the translation key
     * @param args the placeholder values to insert
     * @return the translated message list with placeholders replaced
     */
    @NotNull List<String> localizeList(@NotNull String key, @NotNull Map<String, Object> args);

    /**
     * Gets a translated list of messages for a specific player.
     *
     * @param player the player whose locale should be used
     * @param key    the translation key
     * @return the translated message list
     */
    @NotNull List<String> localizeList(@NotNull UUID player, @NotNull String key);

    /**
     * Gets a translated list of messages for a specific player and replaces placeholders.
     *
     * @param player the player whose locale should be used
     * @param key    the translation key
     * @param args   the placeholder values to insert
     * @return the translated message list with placeholders replaced
     */
    @NotNull List<String> localizeList(@NotNull UUID player, @NotNull String key, @NotNull Map<String, Object> args);
}
