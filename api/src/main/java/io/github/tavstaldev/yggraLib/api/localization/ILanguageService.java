package io.github.tavstaldev.yggraLib.api.localization;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Provides the preferred language locale for a player.
 */
public interface ILanguageService {
    /**
     * Gets the locale for the given player.
     *
     * @param playerId the unique ID of the player
     * @return the locale code, or {@code null} if no locale is available
     */
    @Nullable String getLocale(UUID playerId);
}
