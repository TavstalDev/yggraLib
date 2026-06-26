package io.github.tavstaldev.yggra.items.api.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for working with Adventure {@link Component} text.
 * <p>
 * Provides conversion from legacy Minecraft color codes (&amp;/&sect;) to MiniMessage
 * format, and translation of color-coded strings into Adventure {@link Component} objects.
 */
public final class AdventureUtils {

    private static final Pattern LEGACY_TO_MINI_PATTERN = Pattern.compile("[&§]([0-9a-fk-or])", Pattern.CASE_INSENSITIVE);
    private static final Map<Character, String> LEGACY_TO_MINI = Map.ofEntries(
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
     * Translates color codes using MiniMessage format.
     *
     * @param message     The raw message using MiniMessage syntax.
     * @param checkLegacy Whether to convert legacy '&' and '§' codes to MiniMessage tags before parsing.
     * @return The translated Component message.
     */
    public static Component translateColors(@NotNull String message, boolean checkLegacy) {
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
    public static @NotNull String legacyToMiniMessage(@NotNull String input) {
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
}
