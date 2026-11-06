package io.github.tavstaldev.yggra.impl.v1_20_4.services;

import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardService_1_20_4 {

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

    public void update(Player player, Component title, Component[] rows) {
        var board = ensureExists(player);
        board.updateTitle(title);
        board.updateLines(rows);
    }

    public void updateLine(Player player, Component line, int lineNumber) {
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
