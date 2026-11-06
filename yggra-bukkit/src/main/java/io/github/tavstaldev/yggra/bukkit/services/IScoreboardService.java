package io.github.tavstaldev.yggra.bukkit.services;

import org.bukkit.entity.Player;

public interface IScoreboardService {

    void hideFor(Player player);

    void update(Player player, String title, String[] rows);

    void updateLine(Player player, String line, int lineNumber);
}
