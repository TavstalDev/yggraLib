package io.github.tavstaldev.yggra.core.services;

import io.github.tavstaldev.yggra.core.YggraPlugin;
import io.github.tavstaldev.yggra.core.logger.YggraLogger;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles loading, caching, and retrieving localized strings from YAML locale files.
 * Supports fallback chains, player-specific locales, and automatic migration of
 * the default locale file when a newer version is bundled with the plugin.
 */
public class TranslationService {
    private final Yaml _yaml;
    private final YggraPlugin<?> _plugin;
    private final YggraLogger _logger;
    private String _preferredLocale;
    private boolean _shouldUsePlayerLocale;
    private Map<String, Map<String, Object>> _localization = new ConcurrentHashMap<>();

    /**
     * Creates a new TranslationService and initializes the YAML parser.
     *
     * @param plugin The plugin instance used to access config and resources.
     */
    public TranslationService(YggraPlugin<?> plugin) {
        this._plugin = plugin;
        this._logger = plugin.logger().withModule(TranslationService.class);
        var config = plugin.getConfig();
        this._preferredLocale = config.getString("general.locale", "eng");
        this._shouldUsePlayerLocale = config.getBoolean("general.use-player-locale", true);
        _logger.debug("Preferred locale set to: " + this._preferredLocale);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Forces multi-line formatting
        dumperOptions.setIndent(2);
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setMaxAliasesForCollections(200); // Should be enough
        Constructor constructor = new Constructor(loaderOptions);
        Representer representer = new Representer(dumperOptions);
        this._yaml = new Yaml(constructor, representer);
    }

    /**
     * Reloads config values and re-reads all locale files from disk.
     *
     * @return true if loading succeeded, false otherwise.
     */
    public boolean refresh() {
        var config = _plugin.getConfig();
        this._preferredLocale = config.getString("general.locale", "eng");
        this._shouldUsePlayerLocale = config.getBoolean("general.use-player-locale", true);
        return load();
    }

    /**
     * Reads all YAML locale files from the lang/ directory into memory.
     * Creates the lang/ directory and default eng.yml if they don't exist.
     * If the bundled default locale has a higher version than the local copy,
     * existing keys are merged into the new version.
     *
     * @return true if loading succeeded, false otherwise.
     */
    public boolean load() {
        InputStream inputStream;
        _localization = new ConcurrentHashMap<>();
        _logger.debug("Checking lang directory...");
        Path dirPath = Paths.get(this._plugin.getDataFolder().getPath(), "lang");
        if (!Files.exists(dirPath)) {
            try {
                this._logger.debug("Creating lang directory...");
                Files.createDirectory(dirPath);
            } catch (IOException ex) {
                this._logger.error("Failed to create lang directory.", ex);
                return false;
            }
        }

        // Get default locale (en) from resources
        _logger.debug("Creating lang file...");
        Path defaultLocalePath = Paths.get(dirPath.toString(), "eng.yml");
        if (!Files.exists(defaultLocalePath)) {
            try {
                inputStream = _plugin.getResource("lang/eng.yml");
                if (inputStream == null) {
                    _logger.error("Failed to create the default locale file. Resource 'lang/eng.yml' not found.");
                    return false;
                } else
                    Files.copy(inputStream, defaultLocalePath);
            } catch (IOException ex) {
                _logger.error("Failed to create the default locale file.", ex);
                return false;
            }
        }

        _logger.debug("Reading lang directory...");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            _logger.debug("Reading lang files...");
            for (Path entry : stream) {
                String fileName = entry.getFileName().toString();
                _logger.debug("Reading file: " + fileName);
                if (!(fileName.endsWith(".yml") || fileName.endsWith(".yaml")))
                    continue;

                try
                {
                    inputStream = new FileInputStream(entry.toFile());
                }
                catch (FileNotFoundException ex)
                {
                    _logger.error(String.format("Failed to get localization file. Path: %s", entry));
                    continue;
                }
                catch (Exception ex)
                {
                    _logger.warn("Unknown error happened while reading locale file.");
                    _logger.error(ex.getMessage());
                    continue;
                }

                _logger.debug("Loading yaml file...");
                Object yamlObject = _yaml.load(inputStream);
                if (!(yamlObject instanceof Map))
                {
                    _logger.error("Failed to cast the yamlObject after reading the localization.");
                    continue;
                }

                _logger.debug("Casting yamlObject to Map...");
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> localValue = (Map<String, Object>) yamlObject;
                    String lang = fileName.split("\\.")[0];
                    _localization.put(lang, localValue);
                } catch (Exception ex) {
                    _logger.warn("Failed to cast the yamlObject to Map.");
                    _logger.error(ex.getMessage());
                }
            }
        } catch (IOException ex) {
            _logger.warn("Failed to read the lang directory.");
            _logger.error(ex.getMessage());
            return false;
        }

        // Check if the default locale is outdated and update it if necessary
        Map<String, Object> localValue = _localization.get("eng");
        int fileVersion = 0;
        if (localValue != null && !localValue.isEmpty()) {
            fileVersion = getVersion(localValue);
        }
        try {
            inputStream = _plugin.getResource("lang/eng.yml");
            if (inputStream == null) {
                _logger.debug("Failed to get localization file for locale 'eng'.");
                return false;
            }

            _logger.debug("Loading yaml file...");
            Object yamlObject = _yaml.load(inputStream);
            if (!(yamlObject instanceof Map))
            {
                _logger.error("Failed to cast the yamlObject after reading the localization.");
                return false;
            }

            _logger.debug("Casting yamlObject to Map...");
            @SuppressWarnings("unchecked")
            Map<String, Object> resourceYaml = (Map<String, Object>) yamlObject;
            int resourceVersion = getVersion(resourceYaml);

            if (resourceVersion > fileVersion && localValue != null) {
                for (String key : localValue.keySet()) {
                    if (!resourceYaml.containsKey(key))
                        continue;

                    resourceYaml.put(key, localValue.get(key));
                }
                _localization.put("eng", resourceYaml);
                try (FileWriter writer = new FileWriter(defaultLocalePath.toString())) {
                    _yaml.dump(resourceYaml, writer);
                }
            }
        } catch (Exception ex) {
            _logger.error("Failed to create lang file for locale 'eng'.", ex);
        }
        return true;
    }

    /**
     * Retrieves a localized string for the default locale.
     *
     * @param key The key for the localization string.
     * @return The localized string.
     */
    public @NotNull String localize(@NotNull String key) {
        return getLocalization(_preferredLocale, key);
    }

    /**
     * Retrieves a localized string for the default locale and replaces placeholders with arguments.
     *
     * @param key The key for the localization string.
     * @param args The arguments to replace placeholders in the string.
     * @return The localized string with placeholders replaced.
     */
    public @NotNull String localize(@NotNull String key, @NotNull Map<String, Object> args) {
        String result = getLocalization(_preferredLocale, key);
        if (result.isEmpty())
            return "";
        return replaceArgs(result, args);
    }

    /**
     * Retrieves a localized string for a specific player.
     *
     * @param player The player whose locale is used.
     * @param key The key for the localization string.
     * @return The localized string.
     */
    public @NotNull String localize(@NotNull Player player, @NotNull String key) {
        String locale = getPlayerLocale(player);
        return getLocalization(locale, key);
    }

    /**
     * Retrieves a localized string for a specific player and replaces placeholders with arguments.
     *
     * @param player The player whose locale is used.
     * @param key The key for the localization string.
     * @param args The arguments to replace placeholders in the string.
     * @return The localized string with placeholders replaced.
     */
    public @NotNull String localize(@NotNull Player player, @NotNull String key, @NotNull Map<String, Object> args) {
        String result = localize(player, key);
        if (result.isEmpty())
            return "";
        return replaceArgs(result, args);
    }

    /**
     * Retrieves a localized list of strings for the default locale.
     *
     * @param key The key for the localization list.
     * @return The localized list of strings.
     */
    public @NotNull List<String> localizeList(@NotNull String key) {
        return getLocalizationList(_preferredLocale, key);
    }

    /**
     * Retrieves a localized list of strings for the default locale and replaces placeholders with arguments.
     *
     * @param key The key for the localization list.
     * @param args The arguments to replace placeholders in the list.
     * @return The localized list of strings with placeholders replaced.
     */
    public @NotNull List<String> localizeList(@NotNull String key, @NotNull Map<String, Object> args) {
        List<String> resultList = getLocalizationList(_preferredLocale, key);
        if (resultList.isEmpty())
            return new ArrayList<>();
        return replaceArgs(resultList, args);
    }

    /**
     * Retrieves a localized list of strings for a specific player.
     *
     * @param player The player whose locale is used.
     * @param key The key for the localization list.
     * @return The localized list of strings.
     */
    public @NotNull List<String> localizeList(@NotNull Player player, @NotNull String key) {
        String locale = getPlayerLocale(player);
        return getLocalizationList(locale, key);
    }

    /**
     * Retrieves a localized list of strings for a specific player and replaces placeholders with arguments.
     *
     * @param player The player whose locale is used.
     * @param key The key for the localization list.
     * @param args The arguments to replace placeholders in the list.
     * @return The localized list of strings with placeholders replaced.
     */
    public @NotNull List<String> localizeList(@NotNull Player player, @NotNull String key, @NotNull Map<String, Object> args) {
        List<String> resultList = localizeList(player, key);
        if (resultList.isEmpty())
            return new ArrayList<>();
        return replaceArgs(resultList, args);
    }

    /**
     * Retrieves a localized string for a specific locale and key.
     *
     * @param locale The locale to use.
     * @param key The key for the localization string.
     * @return The localized string.
     */
    private @NotNull String getLocalization(@NotNull String locale, @NotNull String key) {
        try {
            var value = getLocalizationObj(locale, key);
            return String.valueOf(value);
        }
        catch (Exception ex) {
            _logger.warn(String.format("Unknown error happened while translating '%s'.", key));
            _logger.error(ex.getMessage());
            return "";
        }
    }

    /**
     * Retrieves a localized list of strings for a specific locale and key.
     *
     * @param locale The locale to use.
     * @param key The key for the localization list.
     * @return The localized list of strings.
     */
    private @NotNull List<String> getLocalizationList(@NotNull String locale, @NotNull String key) {
        try {
            var value = getLocalizationObj(locale, key);
            if (value instanceof List<?>) {
                return new ArrayList<>((List<String>) value);
            } else {
                _logger.warn(String.format("The value for key '%s' is not a list.", key));
                return new ArrayList<>();
            }
        }
        catch (Exception ex) {
            _logger.warn(String.format("Unknown error happened while translating '%s'.", key));
            _logger.error(ex.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Traverses the locale map hierarchy by splitting the key on dots and returns the resolved value.
     *
     * @param locale The locale to look up.
     * @param key The dot-separated translation key.
     * @return The resolved value at the end of the key path.
     * @throws Exception if an intermediate segment is not a map.
     */
    private @NotNull Object getLocalizationObj(@NotNull String locale, @NotNull String key) throws Exception {
        String[] keys = key.split("\\.");
        Object value = getLocaleMap(locale);
        for (String k : keys) {
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).get(k);
            } else {
                throw new Exception("Failed to get last map of the translation key sequence.");
            }
        }
        return value;
    }

    /**
     * Replaces placeholders in a string with the provided arguments.
     *
     * @param text The string containing placeholders.
     * @param args The arguments to replace placeholders.
     * @return The string with placeholders replaced.
     */
    private @NotNull String replaceArgs(@NotNull String text, @NotNull Map<String, Object> args) {
        String result = text;
        Set<String> argKeys = args.keySet();
        for (@RegExp String dirKey : argKeys) {
            @RegExp String finalKey;
            if (dirKey.startsWith("%"))
                finalKey = dirKey;
            else
                finalKey = "%" + dirKey + "%";

            var argValue = args.get(dirKey);
            if (argValue != null)
                result = result.replace(finalKey, argValue.toString());
        }
        return result;
    }

    /**
     * Replaces placeholders in a list of strings with the provided arguments.
     *
     * @param texts The list of strings containing placeholders.
     * @param args The arguments to replace placeholders.
     * @return The list of strings with placeholders replaced.
     */
    private @NotNull List<String> replaceArgs(@NotNull List<String> texts, @NotNull Map<String, Object> args) {
        List<String> resultList = new ArrayList<>();
        for (String text : texts) {
            resultList.add(replaceArgs(text, args));
        }
        return resultList;
    }

    /**
     * Retrieves the locale map for the given locale, falling back to the preferred locale,
     * then to "eng" if neither is available.
     *
     * @param locale The locale to look up.
     * @return The locale map, or null if no locale is loaded.
     */
    private @NotNull Object getLocaleMap(String locale) {
        Object value = _localization.get(locale);
        if (value == null) {
            value = _localization.get(_preferredLocale);
            if (value == null)
                value = _localization.get("eng");
        }
        return value;
    }

    /**
     * Retrieves the player's locale in ISO 639-3 language code format.
     *
     * @param player The player whose locale is to be retrieved.
     * @return The ISO 639-3 language code of the player's locale, or "en" if an error occurs.
     */
    private @NotNull String getPlayerLocale(@NotNull Player player) {
        try {
            if (!_shouldUsePlayerLocale)
                return _preferredLocale;
            return player.locale().getISO3Language();
        }
        catch (Exception ex) {
            _logger.warn("Failed to get the player's locale.");
            _logger.error(ex.getMessage());
            return _preferredLocale;
        }
    }

    /**
     * Retrieves the file version from the locale map.
     *
     * @param localeMap The locale map containing localization data.
     * @return The file version, or 1 if the key is missing or an error occurs.
     */
    private int getVersion(Map<String, Object> localeMap) {
        try {
            return Integer.parseInt(localeMap.get("file-version").toString());
        }
        catch (Exception ex) {
            _logger.error("Failed to get the file version from the locale map.", ex);
            return 1;
        }
    }
}
