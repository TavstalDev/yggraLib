package io.github.tavstaldev.yggra.core.services;

import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing per-player scoreboards using FastBoard.
 */
public class ScoreboardService {

    private final Map<UUID, FastBoard> boards = new HashMap<>();

    /**
     * Hides and removes the scoreboard for a player.
     *
     * @param player the player whose scoreboard should be removed
     */
    public void hideFor(Player player)
    {
        var uuid = player.getUniqueId();
        if (!boards.containsKey(uuid))
            return;

        var board = boards.get(uuid);
        board.delete();
        boards.remove(uuid);
    }

    /**
     * Updates the title and all rows of a player's scoreboard.
     *
     * @param player the player whose scoreboard to update
     * @param title  the new title
     * @param rows   the new rows
     */
    public void update(Player player, Component title, Component[] rows) {
        var board = ensureExists(player);
        board.updateTitle(title);
        board.updateLines(rows);
    }

    /**
     * Updates a single line on a player's scoreboard.
     *
     * @param player     the player whose scoreboard to update
     * @param line       the new content for the line
     * @param lineNumber the index of the line to update (0-based)
     */
    public void updateLine(Player player, Component line, int lineNumber) {
        var board = ensureExists(player);
        board.updateLine(lineNumber, line);
    }

    /**
     * Retrieves the existing scoreboard for a player, or creates a new one if none exists.
     *
     * @param player the player
     * @return the existing or newly created FastBoard
     */
    private FastBoard ensureExists(Player player) {
        var uuid = player.getUniqueId();
        if (boards.containsKey(uuid))
            return boards.get(uuid);

        FastBoard newBoard = new FastBoard(player);
        boards.put(uuid, newBoard);
        return newBoard;
    }
}
