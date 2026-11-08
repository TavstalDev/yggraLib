package io.github.tavstaldev.yggraLib.bukkit.services;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface ISoundService {
    Optional<Sound> get(@NotNull String name, Sound.Source source, float volume, float pitch);

    Optional<Sound> get(@NotNull String name, Sound.Source source);

    Optional<Sound> get(@NotNull String name);

    Optional<Sound> get(@NotNull String name, float volume, float pitch);

    void play(Player player, Sound sound);

    void stop(Player player, Sound sound);

    void play(List<Player> players, Sound sound);

    void stop(List<Player> players, Sound sound);

    void playAt(Location location, Sound sound);
}
