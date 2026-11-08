package io.github.tavstaldev.yggraLib.impl.vmodern.services;

import io.github.tavstaldev.yggraLib.api.localization.ILanguageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LanguageService_modern implements ILanguageService {
    @Override
    public @Nullable String getLocale(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null)
            return null;
        return player.locale().getISO3Language();
    }
}
