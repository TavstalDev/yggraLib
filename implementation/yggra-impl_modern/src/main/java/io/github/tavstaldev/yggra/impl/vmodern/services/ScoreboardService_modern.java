package io.github.tavstaldev.yggra.impl.vmodern.services;

import fr.mrmicky.fastboard.FastBoard;
import io.github.tavstaldev.yggra.bukkit.services.IScoreboardService;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardService_modern implements IScoreboardService {

    private final Map<UUID, FastBoard> boards = new HashMap<>();

    public void hideFor(Player player)
    {
        var uuid = player.getUniqueId();
        if (!boards.containsKey(uuid))
            return;

        var board = boards.get(uuid);
        board.delete();
        boards.remove(uuid);
    }

    @Override
    public void update(Player player, String title, String[] rows) {
        var board = ensureExists(player);
        board.updateTitle(title);
        board.updateLines(rows);
    }

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
