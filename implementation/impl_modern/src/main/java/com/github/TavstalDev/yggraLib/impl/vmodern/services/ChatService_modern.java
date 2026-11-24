package com.github.TavstalDev.yggraLib.impl.vmodern.services;

import com.github.TavstalDev.yggraLib.api.IYggraPlugin;
import com.github.TavstalDev.yggraLib.api.localization.ITranslationService;
import com.github.TavstalDev.yggraLib.api.logger.YggraLogger;
import com.github.TavstalDev.yggraLib.bukkit.services.IChatService;
import com.github.TavstalDev.yggraLib.bukkit.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * Modern implementation of ChatService for newer Minecraft versions.
 */
public class ChatService_modern implements IChatService {
    private final IYggraPlugin plugin;
    private final YggraLogger logger;
    private final ITranslationService translator;

    /**
     * Creates a new ChatService_modern instance.
     * @param plugin the plugin value
     * @param translationService the translationService value
     */
    public ChatService_modern(IYggraPlugin plugin, ITranslationService translationService) {
        this.plugin = plugin;
        logger = plugin.logger().withModule(ChatService_modern.class);
        translator = translationService;
    }

    /** {@inheritDoc} */
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
    /** {@inheritDoc} */
    @Override
    public void send(@NotNull Player player, Component component) {
        player.sendMessage(component);
    }

    /** {@inheritDoc} */
    public void sendRichMsg(@NotNull Player player, @NotNull String message) {
        player.sendMessage(AdventureUtils.parse(plugin.replacePlaceholders(message), true));
    }

    /** {@inheritDoc} */
    public void sendRichMsg(@NotNull Player player, @NotNull String message, @NotNull Map<String, Object> parameters) {
        String rawMessage = message;

        // Get the keys
        Set<String> keys = parameters.keySet();
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

    /** {@inheritDoc} */
    public void sendLocalizedMsg(@NotNull Player player, @NotNull String key) {
        String rawMessage = translator.localize(player.getUniqueId(), key);
        sendRichMsg(player, plugin.replacePlaceholders(rawMessage));
    }

    /** {@inheritDoc} */
    public void sendLocalizedMsg(@NotNull Player player, @NotNull String key, @NotNull Map<String, Object> parameters) {
        String rawMessage = translator.localize(player.getUniqueId(), key, parameters);
        sendRichMsg(player, plugin.replacePlaceholders(rawMessage));
    }

    /** {@inheritDoc} */
    public void sendCommandReply(CommandSender sender, @NotNull String key) {
        if (sender instanceof Player player) {
            // Send a localized message to the player
            sendLocalizedMsg(player, key);
            return;
        }
        // Log the localized message to the console or other non-player sender
        logger.info(translator.localize(key).replace('&', '§'));
    }

    /** {@inheritDoc} */
    public void sendCommandReply(CommandSender sender, @NotNull String key, Map<String, Object> parameters) {
        if (sender instanceof Player player) {
            // Send a localized message with parameters to the player
            sendLocalizedMsg(player, key, parameters);
            return;
        }
        // Log the localized message with parameters to the console or other non-player sender
        logger.info(translator.localize(key, parameters).replace('&', '§'));
    }
    /** {@inheritDoc} */
    @Override
    public void sendCommandReply(@NotNull CommandSender sender, Component component) {
        sender.sendMessage(component);
    }
}
