package io.github.tavstaldev.yggra.api.localization;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ITranslationService {

    @NotNull String localize(@NotNull String key);

    @NotNull String localize(@NotNull String key, @NotNull Map<String, Object> args);

    @NotNull String localize(@NotNull UUID player, @NotNull String key);

    @NotNull String localize(@NotNull UUID player, @NotNull String key, @NotNull Map<String, Object> args);

    @NotNull List<String> localizeList(@NotNull String key);

    @NotNull List<String> localizeList(@NotNull String key, @NotNull Map<String, Object> args);

    @NotNull List<String> localizeList(@NotNull UUID player, @NotNull String key);

    @NotNull List<String> localizeList(@NotNull UUID player, @NotNull String key, @NotNull Map<String, Object> args);
}
