package io.github.tavstaldev.yggraLib.bootstrap;

import io.github.tavstaldev.yggraLib.api.IPlatform;
import io.github.tavstaldev.yggraLib.api.IYggraConfig;
import io.github.tavstaldev.yggraLib.api.IYggraPlugin;
import io.github.tavstaldev.yggraLib.api.logger.YggraLogger;
import io.github.tavstaldev.yggraLib.bootstrap.models.LibraryInfo;
import io.github.tavstaldev.yggraLib.bukkit.utils.VersionUtils;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
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
    protected final YggraLogger _logger;
    protected final @Nullable Integer _pluginId;
    protected final BukkitLibraryManager libraryManager;
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

    public YggraPlugin(@Nullable Integer pluginId, @Nullable String[] repositories, @Nullable LibraryInfo[] libraries) {
        _pluginId = pluginId;
        _logger = new YggraLogger(this);
        this.libraryManager = new BukkitLibraryManager(this);
        libraryManager.addMavenCentral();
        libraryManager.addSonatype();
        libraryManager.addRepository("https://jitpack.io");
        if (repositories != null) {
            for (String repository : repositories) {
                if (repository == null || repository.isEmpty())
                    continue;

                libraryManager.addRepository(repository);
            }
        }
        if (libraries != null) {
            for (LibraryInfo library : libraries) {
                if (library == null)
                    continue;

                try {
                    Library.Builder builder = Library.builder()
                            .groupId(library.groupId)
                            .artifactId(library.artifactId)
                            .version(library.version);
                    if (library.relocationInfo != null) {
                        builder.relocate(library.relocationInfo.pattern, library.relocationInfo.relocatedPattern);
                    }
                    libraryManager.loadLibrary(builder.build());
                }
                catch (Exception ex) {
                    _logger.error(String.format("Failed to load %s.%s v%s",
                            library.groupId,
                            library.artifactId,
                            library.version
                            ), ex);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public YggraPlugin(@Nullable Integer pluginId, @Nullable LibraryInfo[] libraries) {
        this(pluginId, null, libraries);
    }

    @SuppressWarnings("unused")
    public YggraPlugin(@Nullable Integer pluginId) {
        this(pluginId, null, null);
    }

    public YggraPlugin() {
        this(null, null, null);
    }

    protected void loadDependencies() {
        String packageGroup = "io.github.tavstaldev.yggraLib.shadow";

        // org.yaml:snakeyaml - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("org{}yaml")
                .artifactId("snakeyaml")
                .version("2.7")
                .relocate("org{}yaml", packageGroup + ".yaml")
                .build());

        // dev.triumphteam:triumph-gui - Required for GUI support
        libraryManager.loadLibrary(Library.builder()
                .groupId("dev{}triumphteam")
                .artifactId("triumph-gui")
                .version("3.1.13")
                .relocate("dev{}triumphteam", packageGroup + ".triumphteam")
                .build());

        // fr.mrmicky:fastboard - Required for scoreboard support
        libraryManager.loadLibrary(Library.builder()
                .groupId("fr{}mrmicky")
                .artifactId("fastboard")
                .version("2.2.0")
                .relocate("fr{}mrmicky", packageGroup + ".mrmicky")
                .build());

        //#region Adventure-API

        // net.kyori:examination-api
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("examination-api")
                .version("1.3.0")
                .relocate("net{}kyori", packageGroup + ".kyori")
                .build());

        // net.kyori:examination-string
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("examination-string")
                .version("1.3.0")
                .relocate("net{}kyori", packageGroup + ".kyori")
                .build());

        // net.kyori:adventure-key
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-key")
                .version("4.26.1")
                .relocate("net{}kyori", packageGroup + ".kyori")
                .build());

        // net.kyori:adventure-api - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-api")
                .version("4.26.1")
                .relocate("net{}kyori", packageGroup + ".kyori")
                .build());

        // net.kyori:adventure-text-serializer-legacy - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-legacy")
                .version("4.26.1")
                .relocate("net{}kyori", packageGroup + ".kyori")
                .build());

        // net.kyori:adventure-text-minimessage - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-minimessage")
                .version("4.26.1")
                .relocate("net{}kyori", packageGroup + ".kyori")
                .build());

        // net.kyori:adventure-text-serializer-plain - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-plain")
                .version("4.26.1")
                .relocate("net{}kyori", packageGroup + ".kyori")
                .build());

        // net.kyori:adventure-text-serializer-gson - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-gson")
                .version("4.26.1")
                .relocate("net{}kyori", packageGroup + ".kyori")
                .build());

        //#endregion
    }

    @Override
    public final void onEnable() {
        loadDependencies();
        onPrePluginEnable();
        try {
            String implClassName = getImplClassName();
            IPlatform platform = (IPlatform) Class.forName(implClassName)
                    .getDeclaredConstructor().newInstance();
            platform.setup(this, _serviceCollection);

            if (_pluginId != null) {
                @SuppressWarnings("unused") Metrics metrics = new Metrics(this, _pluginId);
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
    public @NonNull String replacePlaceholders(@NonNull String message) {
        return message.replace("%prefix%", Objects.requireNonNull(config().get("general.prefix", "")));
    }

    private static @NonNull String getImplClassName() {
        String implClassName;

        if (VersionUtils.isAtLeast(1, 21, 5))
            implClassName = "io.github.tavstaldev.yggraLib.impl.v1_21_5.Platform";
        else if (VersionUtils.isAtLeast(1, 21))
            implClassName = "io.github.tavstaldev.yggraLib.impl.v1_21.Platform";
        else if (VersionUtils.isAtLeast(1, 20))
            implClassName = "io.github.tavstaldev.yggraLib.impl.v1_20_4.Platform";
        else if (VersionUtils.isAtLeast(1, 8))
            implClassName = "io.github.tavstaldev.yggraLib.impl.v1_8_8.Platform";
        else
            throw new RuntimeException("Unsupported Minecraft version: " + Bukkit.getBukkitVersion());

        return implClassName;
    }
}
