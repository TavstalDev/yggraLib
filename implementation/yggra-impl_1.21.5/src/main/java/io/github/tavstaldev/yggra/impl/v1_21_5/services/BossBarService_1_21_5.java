package io.github.tavstaldev.yggra.impl.v1_21_5.services;

import io.github.tavstaldev.yggra.bukkit.services.IBossBarService;
import io.github.tavstaldev.yggra.bukkit.utils.AdventureUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class BossBarService_1_21_5 implements IBossBarService {
    /** Tracks all active boss bars by ID. */
    private final HashMap<String, BossBar> bossBars = new HashMap<>();

    public void showFor(String id, Player player) {
        BossBar bossBar = ensureExists(id);
        bossBar.addViewer(player);
    }

    public void hideFor(String id, Player player) {
        BossBar bossBar = ensureExists(id);
        bossBar.removeViewer(player);
    }

    public void hideAll() {
        for (BossBar bossBar : bossBars.values()) {
            bossBar.viewers().forEach(x -> {
                if (x instanceof Player player)
                    bossBar.removeViewer(player);
            });
        }
    }

    public void clearAll() {
        hideAll();
        bossBars.clear();
    }

    public void showFor(String id, List<Player> players) {
        BossBar bossBar = ensureExists(id);
        bossBar.viewers().forEach(x -> {
            if (x instanceof Player player && !players.contains(player)) {
                bossBar.removeViewer(player);
            }
        });
        for (Player player : players) {
            bossBar.addViewer(player);
        }
    }

    public void hideFor(String id, List<Player> players) {
        BossBar bossBar = ensureExists(id);
        for (Player player : players) {
            bossBar.removeViewer(player);
        }
    }

    public void updateProgress(String id, float progress) {
        BossBar bossBar = ensureExists(id);
        bossBar.progress(progress);
    }

    public void updateTitle(String id, String title) {
        BossBar bossBar = ensureExists(id);
        bossBar.name(AdventureUtils.parse(title, true));
    }

    private BossBar ensureExists(String id) {
        if (!bossBars.containsKey(id)) {
            BossBar bossBar = BossBar.bossBar(
                    Component.text(""),
                    1.0f,
                    BossBar.Color.BLUE,
                    BossBar.Overlay.PROGRESS
            );
            bossBars.put(id, bossBar);
            return bossBar;
        }
        return bossBars.get(id);
    }
}
