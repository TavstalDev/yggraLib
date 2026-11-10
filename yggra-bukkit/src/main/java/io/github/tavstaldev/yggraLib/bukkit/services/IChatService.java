package io.github.tavstaldev.yggraLib.bukkit.services;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("unused")
public interface IChatService {

    Component buildWithButtons(String message, Map<String, Component> parameters);

    void send(@NotNull Player player, Component component);

    void sendRichMsg(@NotNull Player player, @NotNull String message);

    void sendRichMsg(@NotNull Player player, @NotNull String message, @NotNull Map<String, Object> parameters);

    void sendLocalizedMsg(@NotNull Player player, @NotNull String key);

    void sendLocalizedMsg(@NotNull Player player, @NotNull String key, @NotNull Map<String, Object> parameters);

    void sendCommandReply(@Nullable CommandSender sender, @NotNull String key);

    void sendCommandReply(@Nullable CommandSender sender, @NotNull String key, Map<String, Object> parameters);

    void sendCommandReply(@NotNull CommandSender sender, Component component);
}
