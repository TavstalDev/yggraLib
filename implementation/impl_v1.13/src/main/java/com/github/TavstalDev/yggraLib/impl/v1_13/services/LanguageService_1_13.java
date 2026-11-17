package com.github.TavstalDev.yggraLib.impl.v1_13.services;

import com.github.TavstalDev.yggraLib.api.localization.ILanguageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Implementation of LanguageService for Minecraft 1.13.
 */
public class LanguageService_1_13 implements ILanguageService {
    @Override
    public @Nullable String getLocale(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null)
            return null;
        return player.getLocale();
    }
}
