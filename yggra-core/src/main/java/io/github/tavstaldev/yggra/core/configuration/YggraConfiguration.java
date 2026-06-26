package io.github.tavstaldev.yggra.core.configuration;

import io.github.tavstaldev.yggra.core.YggraPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Abstract base class for YAML configuration files.
 * Handles file creation (from a resource or blank), loading, saving, and default resolution.
 */
public abstract class YggraConfiguration extends YamlConfiguration {
    private final YggraPlugin plugin;
    private final File _file;

    /**
     * Creates a new configuration file. If the file does not exist it is created,
     * optionally copying from an embedded resource.
     *
     * @param plugin       The plugin instance.
     * @param relativePath The file path relative to the plugin data folder.
     * @param resourcePath An optional embedded resource path to copy from, or null for a blank file.
     */
    public YggraConfiguration(YggraPlugin plugin, String relativePath, @Nullable String resourcePath) {
        this.plugin = plugin;
        Path filePath = Paths.get(plugin.getDataFolder().getPath(), relativePath);
        _file = filePath.toFile();
        if (!_file.exists()) {
            try {
                if (resourcePath != null) {
                    var resourceStream = plugin.getResource(relativePath);
                    if (resourceStream == null) {
                        if (!_file.isDirectory()) {
                            //noinspection ResultOfMethodCallIgnored
                            _file.getParentFile().mkdirs();
                        }
                        //noinspection ResultOfMethodCallIgnored
                        _file.createNewFile();
                    } else {
                        Files.copy(resourceStream, filePath);
                    }
                } else {
                    if (!_file.isDirectory()) {
                        //noinspection ResultOfMethodCallIgnored
                        _file.getParentFile().mkdirs();
                    }
                    //noinspection ResultOfMethodCallIgnored
                    _file.createNewFile();
                }
            } catch (Exception ex) {
                plugin.logger().error("Could not create configuration file: " + filePath);
                plugin.logger().error(ex.getMessage());
            }
        }
        load();
    }

    /**
     * Creates a new blank configuration file.
     *
     * @param plugin       The plugin instance.
     * @param relativePath The file path relative to the plugin data folder.
     */
    public YggraConfiguration(YggraPlugin plugin, String relativePath) {
        this(plugin, relativePath, null);
    }

    /**
     * Loads the configuration file and applies default values.
     * This method must be implemented by subclasses to define default values.
     */
    protected abstract void loadDefaults();

    /**
     * Loads the configuration file from disk.
     * If the file cannot be loaded, an error is logged.
     */
    public void load() {
        try {
            this.load(_file);
        } catch (Exception ex) {
            plugin.logger().error("Could not load configuration file: " + _file.getPath(), ex);
        }
        loadDefaults();
        save();
    }

    /**
     * Saves the configuration file to disk.
     * If the file cannot be saved, an error is logged.
     */
    public void save() {
        try {
            this.save(_file);
        } catch (Exception ex) {
            plugin.logger().error("Could not save configuration file: " + _file.getPath(), ex);
        }
    }

    /**
     * Resolves a configuration path by setting a default value if the path does not exist.
     *
     * @param path  The configuration path.
     * @param value The default value to set if the path does not exist.
     */
    public void resolve(String path, Object value) {
        if (!this.contains(path)) {
            this.set(path, value);
        }
    }

    /**
     * Resolves a configuration path by getting its value or setting a default value if it does not exist.
     *
     * @param path  The configuration path.
     * @param value The default value to set if the path does not exist.
     * @param <T>   The type of the value.
     * @return The existing or default value.
     */
    public <T> T resolveGet(String path, T value) {
        try {
            @SuppressWarnings("unchecked") T local = (T) this.get(path);
            if (local == null) {
                this.set(path, value);
                return value;
            }
            return local;
        } catch (Exception ignored) {
            return value;
        }
    }

    /**
     * Resolves a configuration path by setting a comment if none exists.
     *
     * @param path    The configuration path to resolve.
     * @param comment The comment to set if no comments exist for the path.
     */
    public void resolveComment(String path, String comment) {
        if (this.getComments(path).isEmpty()) {
            this.setComments(path, List.of(comment));
        }
    }

    /**
     * Resolves a configuration path by setting comments if none exist.
     *
     * @param path     The configuration path.
     * @param comments The comments to set.
     */
    public void resolveComment(String path, List<String> comments) {
        if (this.getComments(path).isEmpty())
            this.setComments(path, comments);
    }
}
