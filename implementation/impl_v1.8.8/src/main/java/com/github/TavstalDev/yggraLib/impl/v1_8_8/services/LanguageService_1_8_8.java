package com.github.TavstalDev.yggraLib.impl.v1_8_8.services;

import com.github.TavstalDev.yggraLib.api.localization.ILanguageService;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Implementation of LanguageService for Minecraft 1.8.8.
 */
public class LanguageService_1_8_8 implements ILanguageService {
    /** {@inheritDoc} */
    @Override
    public @Nullable String getLocale(UUID playerId) {
        return null; // there is no way to get the player's locale on 1.8.8
    }
}
