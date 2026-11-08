package io.github.tavstaldev.yggraLib.impl.v1_8_8.services;

import fr.mrmicky.fastboard.FastBoard;
import io.github.tavstaldev.yggraLib.bukkit.services.IScoreboardService;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardService_1_8_8 implements IScoreboardService {

    private final Map<UUID, FastBoard> boards = new HashMap<>();

    public void hideFor(Player player)
    {
        UUID uuid = player.getUniqueId();
        if (!boards.containsKey(uuid))
            return;

        FastBoard board = boards.get(uuid);
        board.delete();
        boards.remove(uuid);
    }

    @Override
    public void update(Player player, String title, String[] rows) {
        FastBoard board = ensureExists(player);
        board.updateTitle(title);
        board.updateLines(rows);
    }

    @Override
    public void updateLine(Player player, String line, int lineNumber) {
        FastBoard board = ensureExists(player);
        board.updateLine(lineNumber, line);
    }

    private FastBoard ensureExists(Player player) {
        UUID uuid = player.getUniqueId();
        if (boards.containsKey(uuid))
            return boards.get(uuid);

        FastBoard newBoard = new FastBoard(player);
        boards.put(uuid, newBoard);
        return newBoard;
    }
}
