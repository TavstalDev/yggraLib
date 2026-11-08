package io.github.tavstaldev.yggraLib.bukkit.services;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface IActionBarService {

    void sendRichMsg(@NotNull Player player, @NotNull String message);

    void sendRichMsg(@NotNull Player player, @NotNull String message, @NotNull Map<String, Object> parameters);

    void sendLocalizedMsg(@NotNull Player player, @NotNull String key);

    void sendLocalizedMsg(@NotNull Player player, @NotNull String key, @NotNull Map<String, Object> parameters);
}
