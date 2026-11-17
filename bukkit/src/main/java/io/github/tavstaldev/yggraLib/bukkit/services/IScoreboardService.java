package io.github.tavstaldev.yggraLib.bukkit.services;

import org.bukkit.entity.Player;

/**
 * Provides methods to show, hide, and update player scoreboards.
 */
public interface IScoreboardService {

    /**
     * Hides the scoreboard for a player.
     *
     * @param player the player
     */
    void hideFor(Player player);

    /**
     * Updates the scoreboard for a player with a title and row values.
     *
     * @param player the player
     * @param title  the scoreboard title
     * @param rows   the scoreboard rows
     */
    void update(Player player, String title, String[] rows);

    /**
     * Updates a single scoreboard line for a player.
     *
     * @param player     the player
     * @param line       the new line text
     * @param lineNumber the line number to update
     */
    void updateLine(Player player, String line, int lineNumber);
}
