package io.github.tavstaldev.yggra.impl.v1_8_8.services;

import io.github.tavstaldev.yggra.api.localization.ILanguageService;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LanguageService_1_8_8 implements ILanguageService {
    @Override
    public @Nullable String getLocale(UUID playerId) {
        return null; // there is no way to get the player's locale on 1.8.8
    }
}
