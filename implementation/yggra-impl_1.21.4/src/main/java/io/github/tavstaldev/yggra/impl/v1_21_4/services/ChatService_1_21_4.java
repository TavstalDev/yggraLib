package io.github.tavstaldev.yggra.impl.v1_21_4.services;

import io.github.tavstaldev.yggra.api.IYggraPlugin;
import io.github.tavstaldev.yggra.api.localization.ITranslationService;
import io.github.tavstaldev.yggra.api.logger.YggraLogger;
import io.github.tavstaldev.yggra.bukkit.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.RegExp;

import java.util.Map;
import java.util.Set;

public class ChatService_1_21_4 {
    private final IYggraPlugin plugin;
    private final YggraLogger logger;
    private final ITranslationService translator;

    public ChatService_1_21_4(IYggraPlugin plugin, ITranslationService translationService) {
        this.plugin = plugin;
        logger = plugin.logger().withModule(ChatService_1_21_4.class);
        translator = translationService;
    }

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

    public void sendRichMsg(Player player, String message) {
        player.sendMessage(AdventureUtils.parse(plugin.replacePlaceholders(message), true));
    }

    public void sendRichMsg(Player player, String message, Map<String, Object> parameters) {
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

    public void sendLocalizedMsg(Player player, String key) {
        String rawMessage = translator.localize(player.getUniqueId(), key);
        sendRichMsg(player, plugin.replacePlaceholders(rawMessage));
    }

    public void sendLocalizedMsg(Player player, String key, Map<String, Object> parameters) {
        String rawMessage = translator.localize(player.getUniqueId(), key);

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

    public void sendCommandReply(CommandSender sender, String key) {
        if (sender instanceof Player player) {
            // Send a localized message to the player
            sendLocalizedMsg(player, key);
            return;
        }
        // Log the localized message to the console or other non-player sender
        logger.info(translator.localize(key).replace('&', '§'));
    }

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
