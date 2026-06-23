package io.github.tavstaldev.yggra.core;

import io.github.tavstaldev.yggra.core.configuration.YggraConfiguration;
import io.github.tavstaldev.yggra.core.logger.YggraLogger;
import io.github.tavstaldev.yggra.core.translation.TranslationProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpClient;
import java.time.Duration;

public class YggraPlugin extends JavaPlugin {

    protected final YggraLogger _logger;
    public YggraLogger logger() {
        return _logger;
    }
    protected final YggraPluginSettings _settings;
    public YggraPluginSettings settings() {
        return _settings;
    }
    protected YggraConfiguration _config;
    public YggraConfiguration config() {
        return _config;
    }
    protected TranslationProvider _translator;
    public TranslationProvider translator() {
        return _translator;
    }
    private final HttpClient _httpClient;
    public HttpClient httpClient() {
        return _httpClient;
    }
    private @Nullable MenuManager _menuManager;
    public @Nullable MenuManager menuManager() {
        return _menuManager;
    }

    public YggraPlugin(YggraPluginSettings settings) {
        _settings = settings;
        _logger = new YggraLogger(this);
        _httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(120))
                .build();
    }

    public YggraPlugin() {
        this(new YggraPluginSettings());
    }

    @Override
    public final void onEnable() {
        if (_settings.enableGui) {
            GuiDupeDetector.register(this);
            _menuManager = new MenuManager(this);
        }
        else {
            _menuManager = null;
        }
        onPluginEnable();
    }

    public void onPluginEnable() {
        // To be overridden by subclasses
    }

    @Override
    public final void onDisable() {
        if (_settings.enableGui) {
            GuiDupeDetector.unregister(this);
        }
        onPluginDisable();
    }

    public void onPluginDisable() {
        // To be overridden by subclasses
    }
}
