package com.github.TavstalDev.yggraLib.bootstrap;

import com.github.TavstalDev.yggraLib.api.IYggraConfig;
import com.github.TavstalDev.yggraLib.api.IYggraPlugin;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Base configuration class for plugins that use YAML files.
 * <p>
 * This class handles loading, saving, and reading configuration values.
 */
public abstract class YggraConfig implements IYggraConfig {
    private final IYggraPlugin plugin;
    private final YamlConfiguration _yaml;
    private final File _file;

    /**
     * Creates a new configuration instance.
     * <p>
     * If the file does not exist, this constructor tries to create it.
     * It can copy a resource file or create a new file with default values.
     *
     * @param plugin the plugin that owns this configuration
     * @param relativePath the path of the configuration file relative to the plugin data folder
     * @param resourcePath an optional resource path hint, or {@code null} if not used
     */
    public YggraConfig(IYggraPlugin plugin, String relativePath, @Nullable String resourcePath) {
        this.plugin = plugin;
        Path filePath = Paths.get(plugin.dataFolder().getPath(), relativePath);
        _file = filePath.toFile();
        _yaml = new YamlConfiguration();
        if (!_file.exists()) {
            try {
                if (resourcePath != null) {
                    InputStream resourceStream = plugin.resource(relativePath);
                    if (resourceStream == null) {
                        if (!_file.isDirectory()) {
                            //noinspection ResultOfMethodCallIgnored
                            _file.getParentFile().mkdirs();
                        }
                        //noinspection ResultOfMethodCallIgnored
                        _file.createNewFile();
                        loadDefaults();
                        save();
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
                    loadDefaults();
                    save();
                }
            } catch (Exception ex) {
                plugin.logger().error("Could not create configuration file: " + filePath);
                plugin.logger().error(ex.getMessage());
            }
        }
        else
            load();
    }

    /**
     * Creates a new configuration instance without a resource path.
     *
     * @param plugin the plugin that owns this configuration
     * @param relativePath the path of the configuration file relative to the plugin data folder
     */
    public YggraConfig(IYggraPlugin plugin, String relativePath) {
        this(plugin, relativePath, null);
    }

    /**
     * Defines the default configuration values.
     * <p>
     * Implementations should use this method to set or resolve required values.
     */
    protected abstract void loadDefaults();

    /**
     * Loads the configuration file from disk and applies default values.
     * <p>
     * After loading, the configuration is saved to keep missing values in the file.
     */
    public void load() {
        try {
            _yaml.load(_file);
        } catch (Exception ex) {
            plugin.logger().error("Could not load configuration file: " + _file.getPath(), ex);
        }
        loadDefaults();
        save();
    }

    /**
     * Saves the current configuration values to disk.
     */
    public void save() {
        try {
            _yaml.save(_file);
        } catch (Exception ex) {
            plugin.logger().error("Could not save configuration file: " + _file.getPath(), ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public <T> T get(String path, T value) {
        try {
            @SuppressWarnings("unchecked") T local = (T) _yaml.get(path);
            if (local == null) {
                return value;
            }
            return local;
        } catch (Exception ignored) {
            return value;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void resolve(String path, Object value) {
        if (!_yaml.contains(path)) {
            _yaml.set(path, value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public <T> T resolveGet(String path, T value) {
        try {
            @SuppressWarnings("unchecked") T local = (T) _yaml.get(path);
            if (local == null) {
                _yaml.set(path, value);
                return value;
            }
            return local;
        } catch (Exception ignored) {
            return value;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void resolveComment(String path, String comment) {
        if (_yaml.getComments(path).isEmpty()) {
            List<String> c = new ArrayList<>();
            c.add(comment);
            _yaml.setComments(path, c);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void resolveComments(String path, List<String> comments) {
        if (_yaml.getComments(path).isEmpty())
            _yaml.setComments(path, comments);
    }
}
