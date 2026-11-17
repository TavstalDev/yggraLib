package io.github.tavstaldev.yggraLib.bukkit.services;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Provides chat-related helper methods for sending messages and building components.
 */
@SuppressWarnings("unused")
public interface IChatService {

    /**
     * Builds a message component and replaces button placeholders with components.
     *
     * @param message    the message template
     * @param parameters the placeholder values to insert
     * @return the built component
     */
    Component buildWithButtons(String message, Map<String, Component> parameters);

    /**
     * Sends a component message to a player.
     *
     * @param player    the player to receive the message
     * @param component the message component
     */
    void send(@NotNull Player player, Component component);

    /**
     * Sends a rich text message to a player.
     *
     * @param player  the player to receive the message
     * @param message the message content
     */
    void sendRichMsg(@NotNull Player player, @NotNull String message);

    /**
     * Sends a rich text message to a player with placeholder values.
     *
     * @param player     the player to receive the message
     * @param message    the message content
     * @param parameters the placeholder values to insert
     */
    void sendRichMsg(@NotNull Player player, @NotNull String message, @NotNull Map<String, Object> parameters);

    /**
     * Sends a localized message to a player.
     *
     * @param player the player to receive the message
     * @param key    the localization key
     */
    void sendLocalizedMsg(@NotNull Player player, @NotNull String key);

    /**
     * Sends a localized message to a player with placeholder values.
     *
     * @param player     the player to receive the message
     * @param key        the localization key
     * @param parameters the placeholder values to insert
     */
    void sendLocalizedMsg(@NotNull Player player, @NotNull String key, @NotNull Map<String, Object> parameters);

    /**
     * Sends a command reply to a sender using a localization key.
     *
     * @param sender the command sender, or {@code null} if unavailable
     * @param key    the localization key
     */
    void sendCommandReply(@Nullable CommandSender sender, @NotNull String key);

    /**
     * Sends a command reply to a sender using a localization key and placeholder values.
     *
     * @param sender     the command sender, or {@code null} if unavailable
     * @param key        the localization key
     * @param parameters the placeholder values to insert
     */
    void sendCommandReply(@Nullable CommandSender sender, @NotNull String key, Map<String, Object> parameters);

    /**
     * Sends a command reply to a sender as a component.
     *
     * @param sender    the command sender
     * @param component the message component
     */
    void sendCommandReply(@NotNull CommandSender sender, Component component);
}
