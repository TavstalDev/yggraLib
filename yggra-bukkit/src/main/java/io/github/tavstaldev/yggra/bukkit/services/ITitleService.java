package io.github.tavstaldev.yggra.bukkit.services;

import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.util.Map;

public interface ITitleService {

    void send(Player player, String key, Map<String, Object> arguments, Title.Times times);

    void send(Player player, String key, Map<String, Object> arguments);

    void send(Player player, String title, String subTitle, Title.Times times);

    void send(Player player, String title, String subTitle);
}
