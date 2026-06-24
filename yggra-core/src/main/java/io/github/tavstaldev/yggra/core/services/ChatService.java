package io.github.tavstaldev.yggra.core.services;

import io.github.tavstaldev.yggra.core.YggraPlugin;
import io.github.tavstaldev.yggra.core.logger.YggraLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for handling chat-related operations such as color translation,
 * placeholder replacement, localization, and sending formatted messages
 * to players and command senders.
 */
public class ChatService {
    private final YggraPlugin plugin;
    private final YggraLogger logger;
    private final TranslationService translator;
    private final Pattern LEGACY_TO_MINI_PATTERN = Pattern.compile("[&§]([0-9a-fk-or])", Pattern.CASE_INSENSITIVE);
    private final Map<Character, String> LEGACY_TO_MINI = Map.ofEntries(
            Map.entry('0', "<black>"),
            Map.entry('1', "<dark_blue>"),
            Map.entry('2', "<dark_green>"),
            Map.entry('3', "<dark_aqua>"),
            Map.entry('4', "<dark_red>"),
            Map.entry('5', "<dark_purple>"),
            Map.entry('6', "<gold>"),
            Map.entry('7', "<gray>"),
            Map.entry('8', "<dark_gray>"),
            Map.entry('9', "<blue>"),
            Map.entry('a', "<green>"),
            Map.entry('b', "<aqua>"),
            Map.entry('c', "<red>"),
            Map.entry('d', "<light_purple>"),
            Map.entry('e', "<yellow>"),
            Map.entry('f', "<white>"),
            Map.entry('l', "<bold>"),
            Map.entry('o', "<italic>"),
            Map.entry('n', "<underlined>"),
            Map.entry('m', "<strikethrough>"),
            Map.entry('k', "<obfuscated>"),
            Map.entry('r', "<reset>")
    );

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
        Component result = translateColors(message, true);
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
     * Translates color codes using MiniMessage format.
     *
     * @param message     The raw message using MiniMessage syntax.
     * @param checkLegacy Whether to convert legacy '&' and '§' codes to MiniMessage tags before parsing.
     * @return The translated Component message.
     */
    public Component translateColors(@NotNull String message, boolean checkLegacy) {
        if (!checkLegacy)
            return MiniMessage.miniMessage()
                    .deserialize(message) // Convert to Component
                    .decoration(TextDecoration.ITALIC, false); // Remove italics by default

        String miniMessageString = legacyToMiniMessage(message);
        return MiniMessage.miniMessage()
                .deserialize(miniMessageString) // Convert to Component
                .decoration(TextDecoration.ITALIC, false); // Remove italics by default
    }

    /**
     * Converts legacy '&' and '§' color codes to MiniMessage tags.
     * <br>
     * This method takes a string containing legacy Minecraft color codes (e.g., '§a', '§l')
     * and replaces them with their corresponding MiniMessage tags (e.g., '<green>', '<bold>').
     * It uses a precompiled regex pattern to identify the legacy codes and a lookup map
     * to determine the appropriate MiniMessage tag for each code.
     *
     * @param input The input string containing legacy color codes.
     * @return A string with MiniMessage tags replacing the legacy color codes.
     */
    public @NotNull String legacyToMiniMessage(@NotNull String input) {
        Matcher matcher = LEGACY_TO_MINI_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            char code = Character.toLowerCase(matcher.group(1).charAt(0));
            String replacement = LEGACY_TO_MINI.getOrDefault(code, "");
            matcher.appendReplacement(sb, replacement);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Sends a colored message to a player after processing placeholders and translating color codes.
     *
     * @param player  The player to send the message to.
     * @param message The raw message containing placeholders and legacy/MiniMessage color codes.
     */
    public void sendRichMsg(Player player, String message) {
        player.sendMessage(translateColors(plugin.replacePlaceholders(message), true));
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
