package io.github.tavstaldev.yggraLib.api;

import io.github.tavstaldev.yggraLib.api.logger.YggraLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Represents the main plugin API used by YggraLib.
 * It gives access to common plugin information, services, files, and helpers.
 */
public interface IYggraPlugin {

    /**
     * Returns the plugin name.
     *
     * @return the plugin name
     */
    String name();

    /**
     * Returns the current plugin version.
     *
     * @return the plugin version
     */
    String version();

    /**
     * Returns the URL used to check for plugin updates.
     *
     * @return the download or update URL
     */
    String downloadUrl();

    /**
     * Returns the plugin configuration.
     *
     * @return the configuration object
     */
    IYggraConfig config();

    /**
     * Returns the logger used by this plugin.
     *
     * @return the plugin logger
     */
    YggraLogger logger();

    /**
     * Gets a registered service by its class type.
     *
     * @param clazz the service class to look for
     * @param <T>   the service type
     * @return the service instance, or {@code null} if it is not registered
     */
    @Nullable <T> T getService(Class<T> clazz);

    /**
     * Gets a registered service by its class type.
     * This method throws an exception if the service is missing.
     *
     * @param clazz the service class to look for
     * @param <T>   the service type
     * @return the service instance
     * @throws Exception if the service is not available
     */
    <T> T getRequiredService(Class<T> clazz) throws Exception;

    /**
     * Returns the plugin data folder.
     *
     * @return the data folder
     */
    File dataFolder();

    /**
     * Opens a resource file from the plugin jar.
     *
     * @param path the resource path inside the jar
     * @return an input stream for the resource, or {@code null} if it does not exist
     */
    InputStream resource(String path);

    /**
     * Returns a command object registered with the given name.
     *
     * @param name the command name
     * @return the command object, or {@code null} if it is not found
     */
    Object command(String name);

    /**
     * Replaces placeholders in a message with plugin values.
     *
     * @param message the message that may contain placeholders
     * @return the message with replaced placeholders
     */
    @NotNull String replacePlaceholders(@NotNull String message);

    /**
     * Checks if the plugin version is up to date.
     *
     * @return a future with {@code true} if up to date, {@code false} if outdated,
     * or {@code null} if the check could not be completed
     */
    CompletableFuture<@Nullable Boolean> isUpToDate();
}
