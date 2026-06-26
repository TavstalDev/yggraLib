package io.github.tavstaldev.yggra.core.services;

import io.github.tavstaldev.yggra.core.YggraPlugin;
import io.github.tavstaldev.yggra.core.logger.YggraLogger;
import io.github.tavstaldev.yggra.items.api.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.RegExp;

import java.util.Map;

/**
 * Service for handling chat-related operations such as color translation,
 * placeholder replacement, localization, and sending formatted messages
 * to players and command senders.
 */
public class ChatService {
    private final YggraPlugin plugin;
    private final YggraLogger logger;
    private final TranslationService translator;

    /**
     * Constructs a new ChatService.
     *
     * @param plugin The YggraPlugin instance, used to access the logger and translator.
     */
    public ChatService(YggraPlugin plugin) {
        this.plugin = plugin;
        logger = plugin.logger().withModule(ChatService.class);
        translator = plugin.translator();
    }

    /**
     * Builds a Component message with buttons by replacing placeholders with the provided parameters.
     *
     * @param message    The raw message containing placeholders.
     * @param parameters The dictionary containing placeholder keys and their corresponding Component values.
     * @return The Component message with buttons.
     */
    public Component buildWithButtons(String message, Map<String, Component> parameters) {
        Component result = AdventureUtils.translateColors(message, true);
        // Get the keys
        var keys = parameters.keySet();
        for (@RegExp var dirKey : keys) {
            Component dirElem = parameters.get(dirKey);
            @RegExp String key;
            if (dirKey.startsWith("%"))
                key = dirKey;
            else
                key = "%" + dirKey + "%";
            if (!message.contains(key))
                continue;

            result = result.replaceText(TextReplacementConfig.builder()
                    .match(key)
                    .replacement(dirElem)
                    .build());
        }
        return result.decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Sends a colored message to a player after processing placeholders and translating color codes.
     *
     * @param player  The player to send the message to.
     * @param message The raw message containing placeholders and legacy/MiniMessage color codes.
     */
    public void sendRichMsg(Player player, String message) {
        player.sendMessage(AdventureUtils.translateColors(plugin.replacePlaceholders(message), true));
    }

    /**
     * Sends a colored message to a player with placeholders replaced by actual values from the provided parameters.
     *
     * @param player     The player to send the message to.
     * @param message    The raw message containing '&' color codes and placeholders.
     * @param parameters The dictionary containing placeholder keys and their corresponding values.
     */
    public void sendRichMsg(Player player, String message, Map<String, Object> parameters) {
        String rawMessage = message;

        // Get the keys
        var keys = parameters.keySet();
        for (@RegExp var dirKey : keys) {
            @RegExp String finalKey;
            if (dirKey.startsWith("%"))
                finalKey = dirKey;
            else
                finalKey = "%" + dirKey + "%";
            rawMessage = rawMessage.replace(finalKey, parameters.get(dirKey).toString());
        }

        sendRichMsg(player, plugin.replacePlaceholders(rawMessage));
    }

    /**
     * Retrieves a localized message, translates color codes, and sends it to a player.
     *
     * @param player     The player to send the message to.
     * @param key        The localization key.
     */
    public void sendLocalizedMsg(Player player, String key) {
        String rawMessage = translator.localize(player, key);
        sendRichMsg(player, plugin.replacePlaceholders(rawMessage));
    }

    /**
     * Retrieves a localized message, replaces placeholders with the provided parameters, translates color codes, and sends it to a player.
     *
     * @param player     The player to send the message to.
     * @param key        The localization key.
     * @param parameters The dictionary containing placeholder keys and their corresponding values.
     */
    public void sendLocalizedMsg(Player player, String key, Map<String, Object> parameters) {
        String rawMessage = translator.localize(player, key);

        // Get the keys
        var keys = parameters.keySet();
        for (@RegExp var dirKey : keys) {
            @RegExp String finalKey;
            if (dirKey.startsWith("%"))
                finalKey = dirKey;
            else
                finalKey = "%" + dirKey + "%";
            rawMessage = rawMessage.replace(finalKey, parameters.get(dirKey).toString());
        }

        sendRichMsg(player, plugin.replacePlaceholders(rawMessage));
    }

    /**
     * Sends a localized command reply to the sender.
     * If the sender is a player, the message is sent using the player's locale.
     * Otherwise, the message is logged to the plugin's custom logger.
     *
     * @param sender The command sender (can be a Player or Console).
     * @param key    The localization key for the message to be sent.
     */
    public void sendCommandReply(CommandSender sender, String key) {
        if (sender instanceof Player player) {
            // Send a localized message to the player
            sendLocalizedMsg(player, key);
            return;
        }
        // Log the localized message to the console or other non-player sender
        logger.info(translator.localize(key).replace('&', '§'));
    }

    /**
     * Sends a localized command reply to the sender with placeholders replaced by provided parameters.
     * If the sender is a player, the message is sent using the player's locale.
     * Otherwise, the message is logged to the plugin's custom logger.
     *
     * @param sender     The command sender (can be a Player or Console).
     * @param key        The localization key for the message to be sent.
     * @param parameters A map of placeholder keys and their corresponding values to replace in the message.
     */
    public void sendCommandReply(CommandSender sender, String key, Map<String, Object> parameters) {
        if (sender instanceof Player player) {
            // Send a localized message with parameters to the player
            sendLocalizedMsg(player, key, parameters);
            return;
        }
        // Log the localized message with parameters to the console or other non-player sender
        logger.info(translator.localize(key, parameters).replace('&', '§'));
    }
}
