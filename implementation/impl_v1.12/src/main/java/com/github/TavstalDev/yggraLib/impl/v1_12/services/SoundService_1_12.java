package com.github.TavstalDev.yggraLib.impl.v1_12.services;

import com.github.TavstalDev.yggraLib.bukkit.services.ISoundService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Implementation of SoundService for Minecraft 1.12.
 */
public class SoundService_1_12 implements ISoundService {

    public Optional<org.bukkit.Sound> get(@NotNull String name) {
        String key = name.toLowerCase(Locale.ROOT);
        try {
            if ("none".equalsIgnoreCase(key))
                return Optional.empty();

            return Optional.of(org.bukkit.Sound.valueOf(name));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    /**
     * Handles the play operation.
     * @param player the player value
     * @param sound the sound value
     * @param volume the volume value
     * @param pitch the pitch value
     */
    public void play(Player player, org.bukkit.Sound sound, float volume, float pitch) {
        if (sound == null)
            return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    /**
     * Handles the play operation.
     * @param players the players value
     * @param sound the sound value
     * @param volume the volume value
     * @param pitch the pitch value
     */
    public void play(List<Player> players, org.bukkit.Sound sound, float volume, float pitch) {
        if (sound == null)
            return;
        players.forEach(x -> x.playSound(x.getLocation(), sound, volume, pitch));
    }

    /**
     * Handles the stop operation.
     * @param player the player value
     * @param sound the sound value
     */
    public void stop(Player player, org.bukkit.Sound sound) {
        if (sound == null)
            return;
        player.stopSound(sound);
    }

    /**
     * Handles the stop operation.
     * @param players the players value
     * @param sound the sound value
     */
    public void stop(List<Player> players, org.bukkit.Sound sound) {
        if (sound == null)
            return;
        players.forEach(x -> x.stopSound(sound));
    }

    /**
     * Handles the playAt operation.
     * @param location the location value
     * @param sound the sound value
     * @param volume the volume value
     * @param pitch the pitch value
     */
    public void playAt(Location location, org.bukkit.Sound sound, float volume, float pitch) {
        location.getWorld().playSound( location, sound, volume, pitch);
    }
}
