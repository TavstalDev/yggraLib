package io.github.tavstaldev.yggra.items.api.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

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

        if (hasSection && !hasAmpersand) {
            return SECTION_SERIALIZER.deserialize(text).decoration(TextDecoration.ITALIC, false);
        }

        if (!hasSection) {
            return AMPERSAND_SERIALIZER.deserialize(text).decoration(TextDecoration.ITALIC, false);
        }

        String cleanLegacy = text.replace('§', '&');
        return SECTION_SERIALIZER.deserialize(cleanLegacy).decoration(TextDecoration.ITALIC, false);
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
