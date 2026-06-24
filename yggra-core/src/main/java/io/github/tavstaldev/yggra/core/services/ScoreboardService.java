package io.github.tavstaldev.yggra.core.services;

import fr.mrmicky.fastboard.adventure.FastBoard;
import io.github.tavstaldev.yggra.core.YggraPlugin;
import io.github.tavstaldev.yggra.core.logger.YggraLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardService {

    private final YggraPlugin plugin;
    private final YggraLogger logger;
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private final Map<String, Map<UUID, FastBoard>> globalBoards = new HashMap<>();

    public ScoreboardService(YggraPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.logger().withModule(ScoreboardService.class);
    }
}
