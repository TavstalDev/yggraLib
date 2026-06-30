package io.github.tavstaldev.yggra.items.api.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final LegacyComponentSerializer AMPERSAND_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();
    private static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();
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
     * Converts a legacy Minecraft color-coded string (using &sect; or &amp; prefixes)
     * into MiniMessage format.
     *
     * @param input the legacy string to convert
     * @return the converted MiniMessage string
     */
    private static @NotNull String legacyToMiniMessage(@NotNull String input) {
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
     * Parses a string into an Adventure {@link Component}, optionally checking for legacy color codes.
     * <p>
     * If {@code checkLegacy} is {@code false}, the input is treated as MiniMessage directly.
     * When enabled, the method detects {@code §} and {@code &} legacy color prefixes and
     * delegates to the appropriate serializer. If both are present, {@code §} is normalized
     * to {@code &} before deserialization.
     *
     * @param text        the input string (MiniMessage or legacy)
     * @param checkLegacy whether to probe for legacy section/ampersand codes
     * @return the parsed {@link Component} with italic decoration disabled
     */
    public static Component parse(@NotNull String text, boolean checkLegacy) {
        if (!checkLegacy) {
            return MINI_MESSAGE.deserialize(text).decoration(TextDecoration.ITALIC, false);
        }

        boolean hasAmpersand = text.indexOf('&') != -1;
        boolean hasSection = text.indexOf('§') != -1;
        if (!hasAmpersand && !hasSection) {
            return MINI_MESSAGE.deserialize(text).decoration(TextDecoration.ITALIC, false);
        }

        if (hasSection) {
            text = text.replace('§', '&');
        }

        String miniMessageText = legacyToMiniMessage(text);
        return MINI_MESSAGE
                .deserialize(miniMessageText)
                .decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Parses a string into an Adventure {@link Component}, automatically detecting legacy codes.
     * <p>
     * Convenience overload of {@link #parse(String, boolean)} with {@code checkLegacy = true}.
     *
     * @param text the input string (MiniMessage or legacy)
     * @return the parsed {@link Component} with italic decoration disabled
     */
    public static Component parse(@NotNull String text) {
        return parse(text, true);
    }

    /**
     * Serializes a {@link Component} to plain text, stripping all formatting.
     *
     * @param component the component to serialize
     * @return plain-text representation
     */
    public static @NotNull String toPlainText(@NotNull Component component) {
        return PLAIN_TEXT.serialize(component);
    }

    /**
     * Serializes a {@link Component} to the MiniMessage string format.
     *
     * @param component the component to serialize
     * @return MiniMessage string
     */
    public static @NotNull String toMiniMessage(@NotNull Component component) {
        return MINI_MESSAGE.serialize(component);
    }

    /**
     * Serializes a {@link Component} to a legacy {@code §}-prefixed string.
     *
     * @param component the component to serialize
     * @return legacy section-sign-encoded string
     */
    public static @NotNull String toLegacy(@NotNull Component component) {
        return SECTION_SERIALIZER.serialize(component);
    }

    /**
     * Serializes a {@link Component} to its JSON (GSON) representation.
     *
     * @param component the component to serialize
     * @return JSON string
     */
    public static @NotNull String toJson(@NotNull Component component) {
        return GSON.serialize(component);
    }

    /**
     * Deserializes a JSON string back into an Adventure {@link Component}.
     *
     * @param text JSON representation of a component
     * @return the deserialized {@link Component}
     */
    public static @NotNull Component fromJson(@NotNull String text) {
        return GSON.deserialize(text);
    }
}
