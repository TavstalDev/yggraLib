package io.github.tavstaldev.yggraLib.impl.vlegacy.services;

import io.github.tavstaldev.yggraLib.api.IYggraPlugin;
import io.github.tavstaldev.yggraLib.bukkit.services.IBossBarService;
import io.github.tavstaldev.yggraLib.bukkit.utils.AdventureUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class BossBarService_legacy implements IBossBarService {
    /** Tracks all active boss bars by ID. */
    private final HashMap<String, BossBar> bossBars = new HashMap<>();
    private final BukkitAudiences adventure;

    public BossBarService_legacy(IYggraPlugin plugin)
    {
        this.adventure = BukkitAudiences.create((JavaPlugin)plugin);
    }

    public void showFor(String id, Player player) {
        BossBar bossBar = ensureExists(id);
        bossBar.addViewer(adventure.player(player));
    }

    public void hideFor(String id, Player player) {
        BossBar bossBar = ensureExists(id);
        bossBar.removeViewer(adventure.player(player));
    }

    public void hideAll() {
        for (BossBar bossBar : bossBars.values()) {
            bossBar.viewers().forEach(x -> {
                if (x instanceof Player)
                    bossBar.removeViewer(adventure.player((Player)x));
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
            if (x instanceof Player) {
                Player player = (Player)x;
                if (!players.contains(player))
                    bossBar.removeViewer(adventure.player(player));
            }
        });
        for (Player player : players) {
            bossBar.addViewer(adventure.player(player));
        }
    }

    public void hideFor(String id, List<Player> players) {
        BossBar bossBar = ensureExists(id);
        for (Player player : players) {
            bossBar.removeViewer(adventure.player(player));
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
