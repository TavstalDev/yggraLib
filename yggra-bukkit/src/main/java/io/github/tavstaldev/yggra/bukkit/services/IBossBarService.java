package io.github.tavstaldev.yggra.bukkit.services;

import org.bukkit.entity.Player;

import java.util.List;

public interface IBossBarService {

    void showFor(String id, Player player);

    void hideFor(String id, Player player);

    void hideAll();

    void clearAll();

    void showFor(String id, List<Player> players);

    void hideFor(String id, List<Player> players);

    void updateProgress(String id, float progress);

    void updateTitle(String id, String title);
}
