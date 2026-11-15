package io.github.tavstaldev.yggraLib.bootstrap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
    public String version() { return this.getDescription().getVersion(); }

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
                //.relocate("dev{}triumphteam", packageGroup + ".triumphteam")
                .build());

        // fr.mrmicky:fastboard - Required for scoreboard support
        libraryManager.loadLibrary(Library.builder()
                .groupId("fr{}mrmicky")
                .artifactId("fastboard")
                .version("2.2.0")
                //.relocate("fr{}mrmicky", packageGroup + ".mrmicky")
                .build());

        // com.github.ben-manes.caffeine - Required for caching
        libraryManager.loadLibrary(Library.builder()
                .groupId("com{}github{}ben-manes{}caffeine")
                .artifactId("caffeine")
                .version("2.9.3")
                //.relocate("com{}github{}ben-manes{}caffeine", packageGroup + ".yaml")
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

    @Override
    public CompletableFuture<@Nullable Boolean> isUpToDate() {
        return CompletableFuture.supplyAsync(() -> {
            _logger.debug("Checking for updates...");
            String downloadUrl = downloadUrl();
            URL url;
            try
            {
                url = new URL(downloadUrl);
            }
            catch (Exception ex) {
                _logger.error("Failed to get downloadURL.", ex);
                return null;
            }

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000); // 5 seconds timeout
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    _logger.error("Up to date check failed with " + responseCode + " status code.");
                    return null;
                }

                String rawResponse;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    rawResponse  = response.toString();
                }

                JsonElement jsonElement = JsonParser.parseString(rawResponse);
                if (jsonElement == null) {
                    _logger.warn("Failed to retrieve the latest version information from " + downloadUrl);
                    return null; // Treat as not up-to-date or an error occurred
                }

                if (!jsonElement.isJsonObject()) {
                    _logger.warn("Expected a JSON object from " + downloadUrl + ", but got: " + jsonElement);
                    return null; // Treat as not up-to-date or an error occurred
                }

                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String latestVersion = jsonObject.get("tag_name").getAsString();
                String currentVersion = version();
                _logger.debug("Current version: " + currentVersion);
                _logger.debug("Latest version: " + latestVersion);

                return currentVersion.equalsIgnoreCase(latestVersion) || ("v" + currentVersion).equalsIgnoreCase(latestVersion);
            }
            catch (Exception ex) {
                _logger.error("Failed to send up to date check.", ex);
                return null;
            }
            finally {
                if (connection != null)
                    connection.disconnect();
            }
        });
    }

    private static @NonNull String getImplClassName() {
        String implClassName;

        if (VersionUtils.isAtLeast(26, 2))
            implClassName = "io.github.tavstaldev.yggraLib.impl.v26_2.Platform";
        else if (VersionUtils.isAtLeast(1, 21, 5))
            implClassName = "io.github.tavstaldev.yggraLib.impl.v1_21_5.Platform";
        else if (VersionUtils.isAtLeast(1, 21))
            implClassName = "io.github.tavstaldev.yggraLib.impl.v1_21.Platform";
        else if (VersionUtils.isAtLeast(1, 20))
            implClassName = "io.github.tavstaldev.yggraLib.impl.v1_20_4.Platform";
        else if (VersionUtils.isAtLeast(1, 14))
            implClassName = "io.github.tavstaldev.yggraLib.impl.v1_14.Platform";
        else if (VersionUtils.isAtLeast(1, 13))
            implClassName = "io.github.tavstaldev.yggraLib.impl.v1_13.Platform";
        else if (VersionUtils.isAtLeast(1, 12))
            implClassName = "io.github.tavstaldev.yggraLib.impl.v1_12.Platform";
        else if (VersionUtils.isAtLeast(1, 8))
            implClassName = "io.github.tavstaldev.yggraLib.impl.v1_8_8.Platform";
        else
            throw new RuntimeException("Unsupported Minecraft version: " + Bukkit.getBukkitVersion());

        return implClassName;
    }
}
