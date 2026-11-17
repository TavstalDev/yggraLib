package io.github.tavstaldev.yggraLib.impl.vlegacy.services;

import io.github.tavstaldev.yggraLib.api.IYggraPlugin;
import io.github.tavstaldev.yggraLib.api.localization.ITranslationService;
import io.github.tavstaldev.yggraLib.api.logger.YggraLogger;
import io.github.tavstaldev.yggraLib.bukkit.services.IChatService;
import io.github.tavstaldev.yggraLib.bukkit.utils.AdventureUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Set;

/**
 * Legacy implementation of ChatService for older Minecraft versions.
 */
public class ChatService_legacy implements IChatService {
    private final IYggraPlugin plugin;
    private final YggraLogger logger;
    private final ITranslationService translator;

    /**
     * Creates a new ChatService_legacy instance.
     * @param plugin the plugin value
     * @param translationService the translationService value
     */
    public ChatService_legacy(IYggraPlugin plugin, ITranslationService translationService) {
        this.plugin = plugin;
        logger = plugin.logger().withModule(ChatService_legacy.class);
        translator = translationService;
    }

    /**
     * Builds a message component and replaces button placeholders.
     * 
     * @param message the source message
     * @param parameters the placeholder replacements
     * @return the built component
     */
    public Component buildWithButtons(String message, Map<String, Component> parameters) {
        Component result = AdventureUtils.parse(message, true);
        // Get the keys
        Set<String> keys = parameters.keySet();
        for (@RegExp String dirKey : keys) {
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
     * Sends a message or reply.
     * @param player the player value
     * @param component the component value
     */

    @Override
    public void send(@NotNull Player player, Component component) {
        sendRichMsg(player, AdventureUtils.toLegacy(component));
    }

    /**
     * Sends a message or reply.
     * @param player the player value
     * @param message the message value
     */
    public void sendRichMsg(@NonNull Player player, @NonNull String message) {
        player.sendMessage(AdventureUtils.toLegacy(AdventureUtils.parse(this.plugin.replacePlaceholders(message))));
    }

    /**
     * Sends a message or reply.
     * @param player the player value
     * @param message the message value
     * @param Map<String the Map<String value
     * @param parameters the parameters value
     */
    public void sendRichMsg(@NonNull Player player, @NonNull String message, @NonNull Map<String, Object> parameters) {
        String rawMessage = message;

        // Get the keys
        Set<String> keys = parameters.keySet();
        for (@RegExp String dirKey : keys) {
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
     * Sends a message or reply.
     * @param player the player value
     * @param key the key value
     */
    public void sendLocalizedMsg(@NonNull Player player, @NonNull String key) {
        String rawMessage = translator.localize(player.getUniqueId(), key);
        sendRichMsg(player, plugin.replacePlaceholders(rawMessage));
    }

    /**
     * Sends a message or reply.
     * @param player the player value
     * @param key the key value
     * @param Map<String the Map<String value
     * @param parameters the parameters value
     */
    public void sendLocalizedMsg(@NonNull Player player, @NonNull String key, @NonNull Map<String, Object> parameters) {
        String rawMessage = translator.localize(player.getUniqueId(), key, parameters);
        sendRichMsg(player, plugin.replacePlaceholders(rawMessage));
    }

    /**
     * Sends a message or reply.
     * @param sender the sender value
     * @param key the key value
     */
    public void sendCommandReply(CommandSender sender, @NonNull String key) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            // Send a localized message to the player
            sendLocalizedMsg(player, key);
            return;
        }
        // Log the localized message to the console or other non-player sender
        logger.info(translator.localize(key).replace('&', ' '));
    }

    /**
     * Sends a message or reply.
     * @param sender the sender value
     * @param key the key value
     * @param Map<String the Map<String value
     * @param parameters the parameters value
     */
    public void sendCommandReply(CommandSender sender, @NonNull String key, Map<String, Object> parameters) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            // Send a localized message with parameters to the player
            sendLocalizedMsg(player, key, parameters);
            return;
        }
        // Log the localized message with parameters to the console or other non-player sender
        logger.info(translator.localize(key, parameters).replace('&', ' '));
    }
    /**
     * Sends a message or reply.
     * @param sender the sender value
     * @param component the component value
     */

    @Override
    public void sendCommandReply(@NotNull CommandSender sender, Component component) {
        if (sender instanceof Player)
        {
            Player player = (Player)sender;
            send(player, component);
            return;
        }

        logger.info(AdventureUtils.toLegacy(component).replace('&', ' '));
    }
}
