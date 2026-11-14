package io.github.tavstaldev.yggraLib.bukkit.services;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface ISoundService {
    Optional<org.bukkit.Sound> get(@NotNull String name);

    void play(Player player, org.bukkit.Sound sound, float volume, float pitch);

    void play(List<Player> players, org.bukkit.Sound sound, float volume, float pitch);

    void stop(Player player, org.bukkit.Sound sound);

    void stop(List<Player> players, org.bukkit.Sound sound);

    void playAt(Location location, org.bukkit.Sound sound, float volume, float pitch);
}
