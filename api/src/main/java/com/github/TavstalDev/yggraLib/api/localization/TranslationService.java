package com.github.TavstalDev.yggraLib.api.localization;

import com.github.TavstalDev.yggraLib.api.IYggraConfig;
import com.github.TavstalDev.yggraLib.api.IYggraPlugin;
import com.github.TavstalDev.yggraLib.api.logger.YggraLogger;
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
 * Loads and manages translated messages from YAML locale files.
 * It supports fallback locales, player-specific locales, and updates for the default locale file.
 */
public class TranslationService implements ITranslationService {
    private final Yaml _yaml;
    private final IYggraPlugin _plugin;
    private final YggraLogger _logger;
    private String _preferredLocale;
    private boolean _shouldUsePlayerLocale;
    private Map<String, Map<String, Object>> _localization = new ConcurrentHashMap<>();

    /**
     * Creates a new TranslationService and initializes the YAML parser.
     *
     * @param plugin The plugin instance used to access config and resources.
     */
    public TranslationService(IYggraPlugin plugin) {
        this._plugin = plugin;
        this._logger = plugin.logger().withModule(TranslationService.class);
        IYggraConfig config = plugin.config();
        this._preferredLocale = config.get("general.locale", "eng");
        this._shouldUsePlayerLocale = config.get("general.use-player-locale", true);
        _logger.debug("Preferred locale set to: " + this._preferredLocale);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Forces multi-line formatting
        dumperOptions.setIndent(2);
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setMaxAliasesForCollections(200); // Should be enough
        Constructor constructor = new Constructor(loaderOptions);
        Representer representer = new Representer(dumperOptions);
        this._yaml = new Yaml(constructor, representer);
        load();
    }

    /**
     * Reloads config values and re-reads all locale files from disk.
     *
     * @return true if loading succeeded, false otherwise.
     */
    public boolean refresh() {
        IYggraConfig config = _plugin.config();
        this._preferredLocale = config.get("general.locale", "eng");
        this._shouldUsePlayerLocale = config.get("general.use-player-locale", true);
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
        Path dirPath = Paths.get(this._plugin.dataFolder().getPath(), "lang");
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
                inputStream = _plugin.resource("lang/eng.yml");
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
            inputStream = _plugin.resource("lang/eng.yml");
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
     * Gets a translated message for the default locale.
     *
     * @param key the translation key
     * @return the translated message
     */
    public @NotNull String localize(@NotNull String key) {
        return getLocalization(_preferredLocale, key);
    }

    /**
     * Gets a translated message for the default locale and replaces placeholders.
     *
     * @param key  the translation key
     * @param args the placeholder values to insert
     * @return the translated message with placeholders replaced
     */
    public @NotNull String localize(@NotNull String key, @NotNull Map<String, Object> args) {
        String result = getLocalization(_preferredLocale, key);
        if (result.isEmpty())
            return "";
        return replaceArgs(result, args);
    }

    /**
     * Gets a translated message for a specific player.
     *
     * @param player the player whose locale should be used
     * @param key    the translation key
     * @return the translated message
     */
    public @NotNull String localize(@NotNull UUID player, @NotNull String key) {
        String locale = getPlayerLocale(player);
        return getLocalization(locale, key);
    }

    /**
     * Gets a translated message for a specific player and replaces placeholders.
     *
     * @param player the player whose locale should be used
     * @param key    the translation key
     * @param args   the placeholder values to insert
     * @return the translated message with placeholders replaced
     */
    public @NotNull String localize(@NotNull UUID player, @NotNull String key, @NotNull Map<String, Object> args) {
        String result = localize(player, key);
        if (result.isEmpty())
            return "";
        return replaceArgs(result, args);
    }

    /**
     * Gets a translated list of messages for the default locale.
     *
     * @param key the translation key
     * @return the translated message list
     */
    public @NotNull List<String> localizeList(@NotNull String key) {
        return getLocalizationList(_preferredLocale, key);
    }

    /**
     * Gets a translated list of messages for the default locale and replaces placeholders.
     *
     * @param key  the translation key
     * @param args the placeholder values to insert
     * @return the translated message list with placeholders replaced
     */
    public @NotNull List<String> localizeList(@NotNull String key, @NotNull Map<String, Object> args) {
        List<String> resultList = getLocalizationList(_preferredLocale, key);
        if (resultList.isEmpty())
            return new ArrayList<>();
        return replaceArgs(resultList, args);
    }

    /**
     * Gets a translated list of messages for a specific player.
     *
     * @param player the player whose locale should be used
     * @param key    the translation key
     * @return the translated message list
     */
    public @NotNull List<String> localizeList(@NotNull UUID player, @NotNull String key) {

        String locale = getPlayerLocale(player);
        return getLocalizationList(locale, key);
    }

    /**
     * Gets a translated list of messages for a specific player and replaces placeholders.
     *
     * @param player the player whose locale should be used
     * @param key    the translation key
     * @param args   the placeholder values to insert
     * @return the translated message list with placeholders replaced
     */
    public @NotNull List<String> localizeList(@NotNull UUID player, @NotNull String key, @NotNull Map<String, Object> args) {
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
            Object value = getLocalizationObj(locale, key);
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
            Object value = getLocalizationObj(locale, key);
            if (value instanceof List<?>) {
                //noinspection unchecked
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

            Object argValue = args.get(dirKey);
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
    private @NotNull String getPlayerLocale(@NotNull UUID player) {
        try {
            if (!_shouldUsePlayerLocale)
                return _preferredLocale;

            ILanguageService languageService = _plugin.getService(ILanguageService.class);
            if (languageService == null)
                return _preferredLocale;

            String locale = languageService.getLocale(player);
            if (locale == null)
                return _preferredLocale;
            return locale;
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
