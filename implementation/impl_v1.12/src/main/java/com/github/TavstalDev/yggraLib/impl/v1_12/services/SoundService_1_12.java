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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void play(Player player, org.bukkit.Sound sound, float volume, float pitch) {
        if (sound == null)
            return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    /** {@inheritDoc} */
    public void play(List<Player> players, org.bukkit.Sound sound, float volume, float pitch) {
        if (sound == null)
            return;
        players.forEach(x -> x.playSound(x.getLocation(), sound, volume, pitch));
    }

    /** {@inheritDoc} */
    public void stop(Player player, org.bukkit.Sound sound) {
        if (sound == null)
            return;
        player.stopSound(sound);
    }

    /** {@inheritDoc} */
    public void stop(List<Player> players, org.bukkit.Sound sound) {
        if (sound == null)
            return;
        players.forEach(x -> x.stopSound(sound));
    }

    /** {@inheritDoc} */
    public void playAt(Location location, org.bukkit.Sound sound, float volume, float pitch) {
        location.getWorld().playSound( location, sound, volume, pitch);
    }
}
