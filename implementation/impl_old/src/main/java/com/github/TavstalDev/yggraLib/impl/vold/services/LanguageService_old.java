package com.github.TavstalDev.yggraLib.impl.vold.services;

import com.github.TavstalDev.yggraLib.api.localization.ILanguageService;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Implementation of LanguageService for Minecraft 1.8.8.
 */
public class LanguageService_old implements ILanguageService {
    /** {@inheritDoc} */
    @Override
    public @Nullable String getLocale(UUID playerId) {
        return null; // there is no way to get the player's locale on 1.8.8
    }
}
