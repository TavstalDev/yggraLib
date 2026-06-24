package io.github.tavstaldev.yggra.core.services;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Manages named {@link BossBar} instances and their viewers.
 * Bars are lazily created on first use and tracked by an ID string.
 */
public class BossBarService {
    /** Used to translate color codes in titles. */
    private final ChatService chat;
    /** Tracks all active boss bars by ID. */
    private final HashMap<String, BossBar> bossBars = new HashMap<>();

    public BossBarService(ChatService chat) {
        this.chat = chat;
    }

    /**
     * Ensures that a BossBar with the given ID exists. If it doesn't, creates a new one.
     *
     * @param id The unique identifier for the BossBar.
     * @return The BossBar associated with the given ID.
     */
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

    /**
     * Displays the BossBar with the given ID to a specific player.
     *
     * @param id The unique identifier for the BossBar.
     * @param player The player to whom the BossBar will be shown.
     */
    public void showFor(String id, Player player) {
        BossBar bossBar = ensureExists(id);
        bossBar.addViewer(player);
    }

    /**
     * Hides the BossBar with the given ID from a specific player.
     *
     * @param id The unique identifier for the BossBar.
     * @param player The player from whom the BossBar will be hidden.
     */
    public void hideFor(String id, Player player) {
        BossBar bossBar = ensureExists(id);
        bossBar.removeViewer(player);
    }

    /**
     * Hides all BossBars from all players.
     */
    public void hideAll() {
        for (BossBar bossBar : bossBars.values()) {
            bossBar.viewers().forEach(x -> {
                if (x instanceof Player player)
                    bossBar.removeViewer(player);
            });
        }
    }

    /**
     * Clears all BossBars by first hiding them from all players
     * and then removing them from the internal storage.
     */
    public void clearAll() {
        hideAll();
        bossBars.clear();
    }

    /**
     * Displays the BossBar with the given ID to a list of players.
     * Removes any players who are no longer in the list from the BossBar's viewers.
     *
     * @param id The unique identifier for the BossBar.
     * @param players The list of players to whom the BossBar will be shown.
     */
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

    /**
     * Hides the BossBar with the given ID from a list of players.
     *
     * @param id The unique identifier for the BossBar.
     * @param players The list of players from whom the BossBar will be hidden.
     */
    public void hideFor(String id, List<Player> players) {
        BossBar bossBar = ensureExists(id);
        for (Player player : players) {
            bossBar.removeViewer(player);
        }
    }

    /**
     * Updates the progress of the BossBar with the given ID.
     *
     * @param id The unique identifier for the BossBar.
     * @param progress The new progress value (0.0 to 1.0).
     */
    public void updateProgress(String id, float progress) {
        BossBar bossBar = ensureExists(id);
        bossBar.progress(progress);
    }

    /**
     * Updates the title of the BossBar with the given ID.
     *
     * @param id The unique identifier for the BossBar.
     * @param title The new title for the BossBar, with color codes translated.
     */
    public void updateTitle(String id, String title) {
        BossBar bossBar = ensureExists(id);
        bossBar.name(chat.translateColors(title, true));
    }
}
