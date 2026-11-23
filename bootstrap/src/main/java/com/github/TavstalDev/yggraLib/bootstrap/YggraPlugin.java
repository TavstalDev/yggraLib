package com.github.TavstalDev.yggraLib.bootstrap;

import com.github.TavstalDev.yggraBossBar.api.IBossBar;
import com.github.TavstalDev.yggraLib.api.IPlatform;
import com.github.TavstalDev.yggraLib.api.IYggraConfig;
import com.github.TavstalDev.yggraLib.api.IYggraPlugin;
import com.github.TavstalDev.yggraLib.api.cache.ICache;
import com.github.TavstalDev.yggraLib.api.database.IDatabase;
import com.github.TavstalDev.yggraLib.api.logger.YggraLogger;
import com.github.TavstalDev.yggraLib.bootstrap.models.LibraryInfo;
import com.github.TavstalDev.yggraLib.bukkit.gui.IGuiService;
import com.github.TavstalDev.yggraLib.bukkit.services.IScoreboardService;
import com.github.TavstalDev.yggraLib.bukkit.utils.VersionUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

/**
 * Base plugin class for plugins that use the Yggra library.
 * <p>
 * This class loads dependencies, prepares the platform implementation,
 * and provides common plugin features.
 *
 * @param <TConfig> the configuration type used by the plugin
 */
public abstract class YggraPlugin<TConfig extends IYggraConfig> extends JavaPlugin implements IYggraPlugin {

    /**
     * Stores registered services by their class type.
     */
    protected final HashMap<Class<?>, Object> _serviceCollection = new HashMap<>();

    /**
     * The logger used by this plugin.
     */
    protected final YggraLogger _logger;

    /**
     * The optional bStats plugin ID.
     */
    protected final @Nullable Integer _pluginId;

    /**
     * The library manager used to load external dependencies at runtime.
     */
    protected final BukkitLibraryManager libraryManager;

    /**
     * The plugin configuration instance.
     */
    protected TConfig _config;

    /**
     * Returns the plugin name.
     *
     * @return the plugin name
     */
    @Override
    public String name() {
        return this.getName();
    }

    /**
     * Returns the plugin version from the plugin description.
     *
     * @return the plugin version
     */
    @Override
    public String version() { return this.getDescription().getVersion(); }

    /**
     * Returns the plugin configuration.
     *
     * @return the configuration instance
     */
    @Override
    public TConfig config() { return _config; }

    /**
     * Returns the plugin logger.
     *
     * @return the logger instance
     */
    @Override
    public YggraLogger logger() {
        return _logger;
    }

    /**
     * Returns a registered service by its class type.
     *
     * @param clazz the service class to look up
     * @param <T> the service type
     * @return the service instance, or {@code null} if no service is registered
     */
    @Override
    public @org.jspecify.annotations.Nullable <T> T getService(Class<T> clazz) {
        Object obj = _serviceCollection.get(clazz);
        if (obj == null)
            return null;
        return clazz.cast(obj);
    }

    /**
     * Returns a required service by its class type.
     *
     * @param clazz the service class to look up
     * @param <T> the service type
     * @return the registered service instance
     * @throws Exception if the service is not registered
     */
    @Override
    public <T> T getRequiredService(Class<T> clazz) throws Exception {
        Object obj = _serviceCollection.get(clazz);
        if (obj == null)
            throw new Exception("Failed to get required service for: " + clazz.getName());

        return clazz.cast(obj);
    }

    /**
     * Returns the plugin data folder.
     *
     * @return the data folder
     */
    @Override
    public File dataFolder() {
        return this.getDataFolder();
    }

    /**
     * Returns a resource from the plugin jar.
     *
     * @param path the resource path
     * @return an input stream for the resource, or {@code null} if it does not exist
     */
    @Override
    public InputStream resource(String path) {
        return this.getResource(path);
    }

    /**
     * Returns a command registered by the plugin.
     *
     * @param name the command name
     * @return the command object, or {@code null} if it is not found
     */
    @Override
    public Object command(String name) {
        return this.getCommand(name);
    }

    /**
     * Creates a new plugin instance and prepares runtime library loading.
     *
     * @param pluginId the optional bStats plugin ID
     * @param repositories additional Maven repositories to register, or {@code null}
     * @param libraries additional libraries to load, or {@code null}
     */
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

    /**
     * Creates a new plugin instance with additional libraries.
     *
     * @param pluginId the optional bStats plugin ID
     * @param libraries additional libraries to load, or {@code null}
     */
    @SuppressWarnings("unused")
    public YggraPlugin(@Nullable Integer pluginId, @Nullable LibraryInfo[] libraries) {
        this(pluginId, null, libraries);
    }

    /**
     * Creates a new plugin instance with an optional bStats plugin ID.
     *
     * @param pluginId the optional bStats plugin ID
     */
    @SuppressWarnings("unused")
    public YggraPlugin(@Nullable Integer pluginId) {
        this(pluginId, null, null);
    }

    /**
     * Creates a new plugin instance with default settings.
     */
    public YggraPlugin() {
        this(null, null, null);
    }

    /**
     * Loads the core libraries required by YggraLib.
     */
    protected void loadDependencies() {
        String packageGroup = "com.github.TavstalDev.yggraLib.shadow";

        // org.yaml:snakeyaml - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("org{}yaml")
                .artifactId("snakeyaml")
                .version("2.7")
                .relocate("org{}yaml", packageGroup + ".yaml")
                .build());

        // dev.triumphteam:triumph-gui - Required for GUI support
        /*libraryManager.loadLibrary(Library.builder()
                .groupId("dev{}triumphteam")
                .artifactId("triumph-gui")
                .version("3.1.13")
                //.relocate("dev{}triumphteam", packageGroup + ".triumphteam")
                .build());*/
        libraryManager.loadLibrary(Library.builder()
                .groupId("com{}github{}lidanthedev{}triumph-gui")
                .artifactId("triumph-gui")
                .version("3.1.14")
                .build());

        // fr.mrmicky:fastboard - Required for scoreboard support
        libraryManager.loadLibrary(Library.builder()
                .groupId("fr{}mrmicky")
                .artifactId("fastboard")
                .version("2.2.2")
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

    /**
     * Enables the plugin.
     * <p>
     * This method loads dependencies, prepares the platform implementation,
     * and then runs the plugin enable hooks.
     */
    @Override
    public final void onEnable() {
        loadDependencies();
        if (!onPrePluginEnable())
            return;
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

    /**
     * Runs before the platform setup is completed.
     * <p>
     * Subclasses can override this method to add custom startup logic.
     */
    public boolean onPrePluginEnable() {
        // To be overridden by subclasses
        return true;
    }

    /**
     * Runs after the platform setup is completed successfully.
     * <p>
     * Subclasses can override this method to add custom startup logic.
     */
    public void onPluginEnable() {
        // To be overridden by subclasses
    }

    /**
     * Disables the plugin and clears registered services.
     */
    @Override
    public final void onDisable() {
        onPrePluginDisable();
        ICache cache = getService(ICache.class);
        if (cache != null)
            cache.evictAll();

        IDatabase database = getService(IDatabase.class);
        if (database != null)
            database.unload();

        IBossBar bossBar = getService(IBossBar.class);
        if (bossBar != null)
            bossBar.clearAll();

        IScoreboardService scoreboard = getService(IScoreboardService.class);
        if (scoreboard != null) {
            for (Player player : Bukkit.getOnlinePlayers())
                scoreboard.hideFor(player);
        }

        IGuiService guiService = getService(IGuiService.class);
        if (guiService != null) {
            guiService.closeAll();
            guiService.unregisterAll();
            guiService.invalidateAllCaches();
        }

        _serviceCollection.clear();
        onPluginDisable();
    }

    /**
     * Runs before registered services are cleaned up during plugin disable.
     * <p>
     * Subclasses can override this method to add custom shutdown logic.
     */
    public void onPrePluginDisable() {
        // To be overridden by subclasses
    }

    /**
     * Runs when the plugin is being disabled.
     * <p>
     * Subclasses can override this method to add custom shutdown logic.
     */
    public void onPluginDisable() {
        // To be overridden by subclasses
    }

    /**
     * Replaces supported placeholders in the given message.
     *
     * @param message the message to process
     * @return the message with placeholders replaced
     */
    @Override
    public @NonNull String replacePlaceholders(@NonNull String message) {
        return message.replace("%prefix%", Objects.requireNonNull(config().get("general.prefix", "")));
    }

    /**
     * Checks whether the current plugin version matches the latest available version.
     * <p>
     * The check is performed asynchronously using the configured download URL.
     *
     * @return a future that resolves to {@code true} if the plugin is up to date,
     * {@code false} if it is not, or {@code null} if the check fails
     */
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

    /**
     * Returns the platform implementation class name for the current Minecraft version.
     *
     * @return the fully qualified platform implementation class name
     * @throws RuntimeException if the current Minecraft version is not supported
     */
    private static @NonNull String getImplClassName() {
        String implClassName;

        if (VersionUtils.isAtLeast(1, 21, 5))
            implClassName = "com.github.TavstalDev.yggraLib.impl.v1_21_5.Platform";
        else if (VersionUtils.isAtLeast(1, 21))
            implClassName = "com.github.TavstalDev.yggraLib.impl.v1_21.Platform";
        else if (VersionUtils.isAtLeast(1, 20))
            implClassName = "com.github.TavstalDev.yggraLib.impl.v1_20_4.Platform";
        else if (VersionUtils.isAtLeast(1, 14))
            implClassName = "com.github.TavstalDev.yggraLib.impl.v1_14.Platform";
        else if (VersionUtils.isAtLeast(1, 13))
            implClassName = "com.github.TavstalDev.yggraLib.impl.v1_13.Platform";
        else if (VersionUtils.isAtLeast(1, 12))
            implClassName = "com.github.TavstalDev.yggraLib.impl.v1_12.Platform";
        else if (VersionUtils.isAtLeast(1, 8))
            implClassName = "com.github.TavstalDev.yggraLib.impl.v1_8_8.Platform";
        else
            throw new RuntimeException("Unsupported Minecraft version: " + Bukkit.getBukkitVersion());

        return implClassName;
    }
}
