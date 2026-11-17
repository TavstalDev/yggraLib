package io.github.tavstaldev.yggraLib.impl.vmodern.services;

import fr.mrmicky.fastboard.FastBoard;
import io.github.tavstaldev.yggraLib.bukkit.services.IScoreboardService;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Modern implementation of ScoreboardService for newer Minecraft versions.
 */
public class ScoreboardService_modern implements IScoreboardService {

    private final Map<UUID, FastBoard> boards = new HashMap<>();

    /**
     * Handles the hideFor operation.
     * @param player the player value
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
     * Handles the update operation.
     * @param player the player value
     * @param title the title value
     * @param rows the rows value
     */

    @Override
    public void update(Player player, String title, String[] rows) {
        var board = ensureExists(player);
        board.updateTitle(title);
        board.updateLines(rows);
    }
    /**
     * Handles the updateLine operation.
     * @param player the player value
     * @param line the line value
     * @param lineNumber the lineNumber value
     */

    @Override
    public void updateLine(Player player, String line, int lineNumber) {
        var board = ensureExists(player);
        board.updateLine(lineNumber, line);
    }

    private FastBoard ensureExists(Player player) {
        var uuid = player.getUniqueId();
        if (boards.containsKey(uuid))
            return boards.get(uuid);

        FastBoard newBoard = new FastBoard(player);
        boards.put(uuid, newBoard);
        return newBoard;
    }
}
