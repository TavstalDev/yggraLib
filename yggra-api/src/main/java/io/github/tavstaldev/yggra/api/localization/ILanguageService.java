package io.github.tavstaldev.yggra.api.localization;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ILanguageService {
    @Nullable String getLocale(UUID playerId);
}
