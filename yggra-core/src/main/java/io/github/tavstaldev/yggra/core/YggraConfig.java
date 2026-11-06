package io.github.tavstaldev.yggra.core;

import io.github.tavstaldev.yggra.api.IYggraConfig;
import io.github.tavstaldev.yggra.api.IYggraPlugin;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class YggraConfig implements IYggraConfig {
    private final IYggraPlugin plugin;
    private final YamlConfiguration _yaml;
    private final File _file;

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

    public YggraConfig(IYggraPlugin plugin, String relativePath) {
        this(plugin, relativePath, null);
    }

    protected abstract void loadDefaults();

    public void load() {
        try {
            _yaml.load(_file);
        } catch (Exception ex) {
            plugin.logger().error("Could not load configuration file: " + _file.getPath(), ex);
        }
        loadDefaults();
        save();
    }

    public void save() {
        try {
            _yaml.save(_file);
        } catch (Exception ex) {
            plugin.logger().error("Could not save configuration file: " + _file.getPath(), ex);
        }
    }

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

    @Override
    public void resolve(String path, Object value) {
        if (!_yaml.contains(path)) {
            _yaml.set(path, value);
        }
    }

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

    @Override
    public void resolveComment(String path, String comment) {
        if (_yaml.getComments(path).isEmpty()) {
            List<String> c = new ArrayList<>();
            c.add(comment);
            _yaml.setComments(path, c);
        }
    }

    @Override
    public void resolveComments(String path, List<String> comments) {
        if (_yaml.getComments(path).isEmpty())
            _yaml.setComments(path, comments);
    }
}
