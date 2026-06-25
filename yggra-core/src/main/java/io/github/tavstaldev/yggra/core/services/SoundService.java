package io.github.tavstaldev.yggra.core.services;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Service for resolving sound names to {@link Sound} objects and playing/stopping them.
 */
public class SoundService {

    /**
     * Resolves a sound name to a {@link Sound} with the given source, volume, and pitch.
     * Returns empty for the name "none" or on lookup failure.
     *
     * @param name   the sound name (e.g. "minecraft:entity.explode")
     * @param source the sound category
     * @param volume the volume
     * @param pitch  the pitch
     * @return an Optional containing the Sound, or empty if not found
     */
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

    /**
     * Resolves a sound name with the given source and default volume/pitch (1.0).
     */
    public Optional<Sound> get(@NotNull String name, Sound.Source source) {
        return get(name, source, 1.0f, 1.0f);
    }

    /**
     * Resolves a sound name with {@link Sound.Source#MASTER} and default volume/pitch (1.0).
     */
    public Optional<Sound> get(@NotNull String name) {
        return get(name, Sound.Source.MASTER, 1.0f, 1.0f);
    }

    /**
     * Resolves a sound name with the given volume/pitch and {@link Sound.Source#MASTER}.
     */
    public Optional<Sound> get(@NotNull String name, float volume, float pitch) {
        return get(name, Sound.Source.MASTER, volume, pitch);
    }

    /**
     * Plays a sound for a single player. Does nothing if the sound is null.
     *
     * @param player the target player
     * @param sound  the sound to play
     */
    public void play(Player player, Sound sound) {
        if (sound == null)
            return;
        player.playSound(sound);
    }

    /**
     * Stops a sound for a single player. Does nothing if the sound is null.
     *
     * @param player the target player
     * @param sound  the sound to stop
     */
    public void stop(Player player, Sound sound) {
        if (sound == null)
            return;
        player.stopSound(sound);
    }

    /**
     * Plays a sound for a list of players. Does nothing if the sound is null.
     *
     * @param players the target players
     * @param sound   the sound to play
     */
    public void play(List<Player> players, Sound sound) {
        if (sound == null)
            return;
        players.forEach(x -> x.playSound(sound));
    }

    /**
     * Stops a sound for a list of players. Does nothing if the sound is null.
     *
     * @param players the target players
     * @param sound   the sound to stop
     */
    public void stop(List<Player> players, Sound sound) {
        if (sound == null)
            return;
        players.forEach(x -> x.stopSound(sound));
    }

    /**
     * Plays a sound at a specific location in its world.
     *
     * @param location the location to play the sound at
     * @param sound    the sound to play
     */
    public void playAt(Location location, Sound sound) {
        location.getWorld().playSound(sound, location.getX(), location.getY(), location.getZ());
    }
}
