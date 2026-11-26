package com.github.TavstalDev.yggraLib.impl.vlegacy.services;

import com.github.TavstalDev.yggraLib.api.localization.ILanguageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Implementation of LanguageService for Minecraft 1.12.
 */
public class LanguageService_legacy implements ILanguageService {
    /** {@inheritDoc} */
    @Override
    public @Nullable String getLocale(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null)
            return null;
        return player.getLocale();
    }
}
