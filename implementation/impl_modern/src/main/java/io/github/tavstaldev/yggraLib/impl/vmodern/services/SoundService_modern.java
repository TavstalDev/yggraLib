package io.github.tavstaldev.yggraLib.impl.vmodern.services;

import io.github.tavstaldev.yggraLib.bukkit.services.ISoundService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SoundService_modern implements ISoundService {

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

    public void play(Player player, org.bukkit.Sound sound, float volume, float pitch) {
        if (sound == null)
            return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void play(List<Player> players, org.bukkit.Sound sound, float volume, float pitch) {
        if (sound == null)
            return;
        players.forEach(x -> x.playSound(x.getLocation(), sound, volume, pitch));
    }

    public void stop(Player player, org.bukkit.Sound sound) {
        if (sound == null)
            return;
        player.stopSound(sound);
    }

    public void stop(List<Player> players, org.bukkit.Sound sound) {
        if (sound == null)
            return;
        players.forEach(x -> x.stopSound(sound));
    }

    public void playAt(Location location, org.bukkit.Sound sound, float volume, float pitch) {
        location.getWorld().playSound( location, sound, volume, pitch);
    }
}
