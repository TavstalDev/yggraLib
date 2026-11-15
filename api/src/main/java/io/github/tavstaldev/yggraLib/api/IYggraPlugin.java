package io.github.tavstaldev.yggraLib.api;

import io.github.tavstaldev.yggraLib.api.logger.YggraLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface IYggraPlugin {
    String name();

    String version();

    String downloadUrl();

    IYggraConfig config();

    YggraLogger logger();

    @Nullable <T> T getService(Class<T> clazz);

    <T> T getRequiredService(Class<T> clazz) throws Exception;

    File dataFolder();

    InputStream resource(String path);

    Object command(String name);

    @NotNull String replacePlaceholders(@NotNull String message);

    CompletableFuture<@Nullable Boolean> isUpToDate();
}
