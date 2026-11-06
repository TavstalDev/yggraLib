package io.github.tavstaldev.yggra.core;

import io.github.tavstaldev.yggra.api.IPlatform;
import io.github.tavstaldev.yggra.api.IYggraConfig;
import io.github.tavstaldev.yggra.api.IYggraPlugin;
import io.github.tavstaldev.yggra.api.logger.YggraLogger;
import io.github.tavstaldev.yggra.bukkit.utils.VersionUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

public abstract class YggraPlugin<TConfig extends IYggraConfig> extends JavaPlugin implements IYggraPlugin {

    protected final HashMap<Class<?>, Object> _serviceCollection = new HashMap<>();
    private final YggraLogger _logger;
    private final @Nullable Integer _pluginId;
    protected TConfig _config;

    @Override
    public String name() {
        return this.getName();
    }

    @Override
    public TConfig config() { return _config; }

    @Override
    public YggraLogger logger() {
        return _logger;
    }

    @Override
    public @org.jspecify.annotations.Nullable <T> T getService(Class<T> clazz) {
        Object obj = _serviceCollection.get(clazz);
        if (obj == null)
            return null;
        return clazz.cast(obj);
    }

    @Override
    public <T> T getRequiredService(Class<T> clazz) throws Exception {
        Object obj = _serviceCollection.get(clazz);
        if (obj == null)
            throw new Exception("Failed to get required service for: " + clazz.getName());

        return clazz.cast(obj);
    }

    @Override
    public File dataFolder() {
        return this.getDataFolder();
    }

    @Override
    public InputStream resource(String path) {
        return this.getResource(path);
    }

    @Override
    public Object command(String name) {
        return this.getCommand(name);
    }

    public YggraPlugin(@Nullable Integer pluginId) {
        _pluginId = pluginId;
        _logger = new YggraLogger(this);
    }

    public YggraPlugin() {
        this(null);
    }

    @Override
    public final void onEnable() {
        onPrePluginEnable();
        try {
            String implClassName = getImplClassName();
            IPlatform platform = (IPlatform) Class.forName(implClassName)
                    .getDeclaredConstructor().newInstance();
            platform.setup(this, _serviceCollection);

            if (_pluginId != null) {
                Metrics metrics = new Metrics(this, _pluginId);
            }
        }
        catch (Exception ex) {
            _logger.error("Failed to initialize the plugin.", ex);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        onPluginEnable();
    }

    public void onPrePluginEnable() {
        // To be overridden by subclasses
    }

    public void onPluginEnable() {
        // To be overridden by subclasses
    }

    @Override
    public final void onDisable() {
        _serviceCollection.clear();
        onPluginDisable();
    }

    public void onPluginDisable() {
        // To be overridden by subclasses
    }

    @Override
    public @NonNull String replacePlaceholders(String message) {
        return message.replace("%prefix%", Objects.requireNonNull(config().get("general.prefix", "")));
    }

    private static @NonNull String getImplClassName() {
        String implClassName;

        if (VersionUtils.isAtLeast(1, 21, 5))
            implClassName = "io.github.tavstaldev.yggra.impl.v1_21_5.Platform";
        else if (VersionUtils.isAtLeast(1, 21))
            implClassName = "io.github.tavstaldev.yggra.impl.v1_21.Platform";
        else if (VersionUtils.isAtLeast(1, 20))
            implClassName = "io.github.tavstaldev.yggra.impl.v1_20_4.Platform";
        else if (VersionUtils.isAtLeast(1, 8))
            implClassName = "io.github.tavstaldev.yggra.impl.v1_8_8.Platform";
        else
            throw new RuntimeException("Unsupported Minecraft version: " + Bukkit.getBukkitVersion());

        return implClassName;
    }
}
