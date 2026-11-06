package io.github.tavstaldev.yggra.impl.v1_21_4.services;

import io.github.tavstaldev.yggra.api.localization.ILanguageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LanguageService_1_21_4 implements ILanguageService {
    @Override
    public @Nullable String getLocale(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null)
            return null;
        return player.locale().getISO3Language();
    }
}
