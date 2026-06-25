package io.github.tavstaldev.yggra.core;

import io.github.tavstaldev.yggra.core.gui.GuiManager;
import io.github.tavstaldev.yggra.core.services.*;
import io.github.tavstaldev.yggra.core.cache.ICache;
import io.github.tavstaldev.yggra.core.configuration.YggraConfiguration;
import io.github.tavstaldev.yggra.core.database.IDatabase;
import io.github.tavstaldev.yggra.core.gui.GuiDupeDetector;
import io.github.tavstaldev.yggra.core.logger.YggraLogger;
import io.github.tavstaldev.yggra.core.scheduler.FoliaScheduler;
import io.github.tavstaldev.yggra.core.scheduler.IScheduler;
import io.github.tavstaldev.yggra.core.scheduler.PaperScheduler;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;

public class YggraPlugin extends JavaPlugin {

    protected final YggraLogger _logger;
    public YggraLogger logger() {
        return _logger;
    }
    protected YggraConfiguration _config;
    public YggraConfiguration config() {
        return _config;
    }
    protected TranslationService _translator;
    public TranslationService translator() {
        return _translator;
    }
    protected IScheduler _scheduler;
    public IScheduler scheduler() {
        return _scheduler;
    }
    protected ChatService _chat;
    public ChatService chat() {
        return _chat;
    }
    protected BossBarService _bossBar;
    public BossBarService bossBar() {
        return _bossBar;
    }
    protected TitleService _title;
    public TitleService title() {
        return _title;
    }
    protected ScoreboardService _scoreboard;
    public ScoreboardService scoreboard() {return _scoreboard;}
    protected SoundService _sound;
    public SoundService sound() {return _sound;}
    private final HttpClient _httpClient;
    public HttpClient httpClient() {
        return _httpClient;
    }
    protected @Nullable ICache _cache;
    public @Nullable ICache cache() {
        return _cache;
    }
    protected @Nullable IDatabase _database;
    public @Nullable IDatabase database() {
        return _database;
    }
    private @Nullable GuiManager _gui;
    public @Nullable GuiManager gui() {
        return _gui;
    }

    public YggraPlugin(@Nullable Integer pluginId) {
        _logger = new YggraLogger(this);
        _httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(120))
                .build();
        _chat = new ChatService(this);
        _bossBar = new BossBarService(_chat);
        _title = new TitleService(this, _chat);
        _scoreboard = new ScoreboardService();
        _sound = new SoundService();
        if (pluginId != null) {
            Metrics metrics = new Metrics(this, pluginId);
        }
    }

    public YggraPlugin() {
        this(null);
    }

    @Override
    public final void onEnable() {
        if (_gui != null) {
            GuiDupeDetector.register(this);
        }
        if (isFolia()) {
            _scheduler = new FoliaScheduler(this);
        } else {
            _scheduler = new PaperScheduler(this);
        }
        onPluginEnable();
    }

    public void onPluginEnable() {
        // To be overridden by subclasses
    }

    @Override
    public final void onDisable() {
        if (_gui != null) {
            GuiDupeDetector.unregister(this);
        }
        onPluginDisable();
    }

    public void onPluginDisable() {
        // To be overridden by subclasses
    }

    public String replacePlaceholders(String message) {
        return message.replace("%prefix%", Objects.requireNonNull(_config.getString("prefix")));
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
