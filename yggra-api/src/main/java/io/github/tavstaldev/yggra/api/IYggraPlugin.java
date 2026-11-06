package io.github.tavstaldev.yggra.api;

import io.github.tavstaldev.yggra.api.logger.YggraLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;

public interface IYggraPlugin {
    String name();

    IYggraConfig config();

    YggraLogger logger();

    @Nullable <T> T getService(Class<T> clazz);

    <T> T getRequiredService(Class<T> clazz) throws Exception;

    File dataFolder();

    InputStream resource(String path);

    Object command(String name);

    @NotNull String replacePlaceholders(@NotNull String message);

}
