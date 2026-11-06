package io.github.tavstaldev.yggra.impl.v1_21.services;

import io.github.tavstaldev.yggra.bukkit.services.ISoundService;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SoundService_1_21 implements ISoundService {

    public Optional<Sound> get(@NotNull String name, Sound.Source source, float volume, float pitch) {
        @SuppressWarnings("PatternValidation")
        String key = name.toLowerCase(Locale.ROOT);
        try {
            if ("none".equalsIgnoreCase(key))
                return Optional.empty();

            @SuppressWarnings("PatternValidation")
            Key soundKey = Key.key(key);

            return Optional.of(Sound.sound(
                    soundKey,
                    source,
                    volume,
                    pitch
            ));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public Optional<Sound> get(@NotNull String name, Sound.Source source) {
        return get(name, source, 1.0f, 1.0f);
    }

    public Optional<Sound> get(@NotNull String name) {
        return get(name, Sound.Source.MASTER, 1.0f, 1.0f);
    }

    public Optional<Sound> get(@NotNull String name, float volume, float pitch) {
        return get(name, Sound.Source.MASTER, volume, pitch);
    }

    public void play(Player player, Sound sound) {
        if (sound == null)
            return;
        player.playSound(sound);
    }

    public void stop(Player player, Sound sound) {
        if (sound == null)
            return;
        player.stopSound(sound);
    }

    public void play(List<Player> players, Sound sound) {
        if (sound == null)
            return;
        players.forEach(x -> x.playSound(sound));
    }

    public void stop(List<Player> players, Sound sound) {
        if (sound == null)
            return;
        players.forEach(x -> x.stopSound(sound));
    }

    public void playAt(Location location, Sound sound) {
        location.getWorld().playSound(sound, location.getX(), location.getY(), location.getZ());
    }
}
