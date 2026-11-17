package com.github.TavstalDev.yggraLib.bukkit.services;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Provides methods to resolve and play sounds for players or locations.
 */
public interface ISoundService {

    /**
     * Resolves a Bukkit sound by name.
     *
     * @param name the sound name
     * @return an optional containing the sound if found
     */
    Optional<org.bukkit.Sound> get(@NotNull String name);

    /**
     * Plays a sound for a player.
     *
     * @param player the player
     * @param sound  the sound to play
     * @param volume the volume level
     * @param pitch  the pitch value
     */
    void play(Player player, org.bukkit.Sound sound, float volume, float pitch);

    /**
     * Plays a sound for multiple players.
     *
     * @param players the players
     * @param sound   the sound to play
     * @param volume  the volume level
     * @param pitch   the pitch value
     */
    void play(List<Player> players, org.bukkit.Sound sound, float volume, float pitch);

    /**
     * Stops a sound for a player.
     *
     * @param player the player
     * @param sound  the sound to stop
     */
    void stop(Player player, org.bukkit.Sound sound);

    /**
     * Stops a sound for multiple players.
     *
     * @param players the players
     * @param sound   the sound to stop
     */
    void stop(List<Player> players, org.bukkit.Sound sound);

    /**
     * Plays a sound at a specific location.
     *
     * @param location the location
     * @param sound    the sound to play
     * @param volume   the volume level
     * @param pitch    the pitch value
     */
    void playAt(Location location, org.bukkit.Sound sound, float volume, float pitch);
}
