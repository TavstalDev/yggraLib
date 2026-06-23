package io.github.tavstaldev.yggra.core.logger;

import io.github.tavstaldev.yggra.core.YggraPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.*;

/**
 * Logger wrapper for Yggra plugins.
 * Provides colored, module-scoped logging with debug toggle support.
 */
public class YggraLogger {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private final YggraPlugin _plugin;
    private final Logger _logger;
    private final String _module;
    private boolean _debugEnabled;

    /**
     * Creates a new logger without a module scope.
     *
     * @param plugin The plugin instance used to read configuration.
     */
    public YggraLogger(YggraPlugin plugin) {
        _plugin = plugin;
        _module = null;
        _logger = Logger.getLogger(plugin.getName());
    }

    /**
     * Creates a new logger with the specified module name.
     *
     * @param plugin The plugin instance used to read configuration.
     * @param module The module name to include in log messages.
     */
    public YggraLogger(YggraPlugin plugin, String module) {
        _plugin = plugin;
        _module = module;
        _logger = Logger.getLogger(plugin.getName());
    }

    /**
     * Creates a new PluginLogger instance with the specified module.
     *
     * @param module The module name to be used in log messages.
     * @return A new PluginLogger instance with the specified module.
     */
    public YggraLogger withModule(String module) {
        return new YggraLogger(_plugin, module);
    }

    /**
     * Creates a new PluginLogger instance with the specified module.
     *
     * @param module The class whose name will be used as the module name in log messages.
     * @return A new PluginLogger instance with the specified module.
     */
    public YggraLogger withModule(@NotNull Class<?> module) {
        return new YggraLogger(_plugin,module.getSimpleName());
    }

    /**
     * Refreshes the debug enabled flag from the plugin configuration.
     * Reads the "debug" key from config.yml, defaulting to false.
     */
    public void refreshDebugEnabled() {
        _debugEnabled = _plugin.getConfig().getBoolean("debug", false);
    }

    /**
     * Logs a message with color coding based on the log level.
     *
     * @param level The log level determining the color.
     * @param text  The message to log.
     * @param ex    An optional exception to append after the message.
     */
    private void logRich(@NotNull Level level, @NotNull String text, @Nullable Exception ex) {
        String moduleText = "";
        if (_module != null)
            moduleText = String.format(" [%s]", _module);

        String color = switch (level.intValue()) {
            case 1000 -> // SEVERE
                    ANSI_RED;
            case 900 -> // WARNING
                    ANSI_YELLOW;
            case 800 -> // INFO
                    ANSI_CYAN;
            case 700 -> // CONFIG
                    ANSI_PURPLE; // FINE
            // FINER
            case 500, 400, 300 -> // FINEST
                    ANSI_GREEN;
            default -> ANSI_WHITE;
        };

        _logger.log(level, String.format("%s%s: %s\u001B[0m", color, moduleText, text));
        if (ex != null)
            _logger.log(level, String.format("%s└── Exception: %s: %s\u001B[0m", color, ex.getClass().getTypeName(), ex.getMessage()));
    }

    /**
     * Logs an informational message.
     *
     * @param text The message to log.
     * @param ex   An optional exception to append after the message.
     */
    public void info(@NotNull String text, @Nullable Exception ex) {
        logRich(INFO, text, ex);
    }

    /**
     * Logs an informational message.
     *
     * @param text The message to log.
     */
    public void info(@NotNull String text) {
        logRich(INFO, text, null);
    }

    /**
     * Logs a success message with a green color code.
     * This method uses rich text formatting to prepend a green color code
     * to the log message, indicating a successful operation.
     *
     * @param text The message to log, which can be any object.
     *             The object will be converted to a string representation.
     * @param ex   An optional exception to append after the message.
     */
    public void ok(@NotNull String text, @Nullable Exception ex) {
        logRich(INFO, text, ex);
    }

    /**
     * Logs a success message with a green color code.
     * This method uses rich text formatting to prepend a green color code
     * to the log message, indicating a successful operation.
     *
     * @param text The message to log, which can be any object.
     *             The object will be converted to a string representation.
     */
    public void ok(@NotNull String text) {
        logRich(INFO, text, null);
    }

    /**
     * Logs a warning message.
     *
     * @param text The message to log.
     * @param ex   An optional exception to append after the message.
     */
    public void warn(@NotNull String text, @Nullable Exception ex) {
        logRich(WARNING, text, ex);
    }

    /**
     * Logs a warning message.
     *
     * @param text The message to log.
     */
    public void warn(@NotNull String text) {
        logRich(WARNING, text, null);
    }

    /**
     * Logs an error message.
     *
     * @param text The message to log.
     * @param ex   An optional exception to append after the message.
     */
    public void error(@NotNull String text, @Nullable Exception ex) {
        logRich(SEVERE, text, ex);
    }

    /**
     * Logs an error message.
     *
     * @param text The message to log.
     */
    public void error(@NotNull String text) {
        logRich(SEVERE, text, null);
    }

    /**
     * Logs a debug message if debugging is enabled in the configuration.
     *
     * @param text The message to log.
     * @param ex   An optional exception to append after the message.
     */
    public void debug(@NotNull String text, @Nullable Exception ex) {
        if (_debugEnabled)
            logRich(FINE, text, ex);
    }

    /**
     * Logs a debug message if debugging is enabled in the configuration.
     *
     * @param text The message to log.
     */
    public void debug(@NotNull String text) {
        if (_debugEnabled)
            logRich(FINE, text, null);
    }
}
