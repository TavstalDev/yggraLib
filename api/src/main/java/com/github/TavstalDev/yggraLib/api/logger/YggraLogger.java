package com.github.TavstalDev.yggraLib.api.logger;

import com.github.TavstalDev.yggraLib.api.IYggraPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.*;

/**
 * Logger wrapper for Yggra plugins.
 * Provides colored, module-scoped logging with debug toggle support.
 */
@SuppressWarnings("unused")
public class YggraLogger {
    /** ANSI escape sequence for red text. */
    public static final String ANSI_RED = "\u001B[31m";
    /** ANSI escape sequence for green text. */
    public static final String ANSI_GREEN = "\u001B[32m";
    /** ANSI escape sequence for yellow text. */
    public static final String ANSI_YELLOW = "\u001B[33m";
    /** ANSI escape sequence for purple text. */
    public static final String ANSI_PURPLE = "\u001B[35m";
    /** ANSI escape sequence for cyan text. */
    public static final String ANSI_CYAN = "\u001B[36m";
    /** ANSI escape sequence for white text. */
    public static final String ANSI_WHITE = "\u001B[37m";
    private final IYggraPlugin _plugin;
    private final Logger _logger;
    private final String _module;
    private boolean _debugEnabled;

    /**
     * Creates a new logger without a module scope.
     *
     * @param plugin The plugin instance used to read configuration.
     */
    public YggraLogger(IYggraPlugin plugin) {
        _plugin = plugin;
        _module = plugin.name();
        _logger = Logger.getLogger(plugin.name());
    }

    /**
     * Creates a new logger with the specified module name.
     *
     * @param plugin The plugin instance used to read configuration.
     * @param module The module name to include in log messages.
     */
    public YggraLogger(IYggraPlugin plugin, String module) {
        _plugin = plugin;
        String pluginName = plugin.name();
        if (!Objects.equals(module, pluginName))
            _module = pluginName + " - " + module;
        else
            _module = module;
        _logger = Logger.getLogger(pluginName);
    }

    /**
     * Creates a new logger that uses the given module name.
     *
     * @param module the module name to include in log messages
     * @return a new logger instance with the specified module
     */
    public YggraLogger withModule(String module) {
        return new YggraLogger(_plugin, module);
    }

    /**
     * Creates a new logger that uses the simple name of the given class as module name.
     *
     * @param module the class whose simple name will be used in log messages
     * @return a new logger instance with the specified module
     */
    public YggraLogger withModule(@NotNull Class<?> module) {

        return new YggraLogger(_plugin, module.getSimpleName());
    }

    /**
     * Updates the debug setting from the plugin configuration.
     * It reads the {@code general.debug-mode} key and defaults to {@code false}.
     */
    public void refreshDebugEnabled() {
        _debugEnabled = _plugin.config().get("general.debug-mode", false);
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
            moduleText = String.format("[%s] ", _module);

        String color;
        switch (level.intValue())
        {
            case 1000:
            {
                color = ANSI_RED;
                break;
            }
            case 900:
            {
                color = ANSI_YELLOW;
                break;
            }
            case 800:
            {
                color = ANSI_CYAN;
                break;
            }
            case 700:
            {
                color = ANSI_PURPLE;
                break;
            }
            case 500:
            case 400:
            case 300:
            {
                color = ANSI_GREEN;
                break;
            }
            default: {
                color = ANSI_WHITE;
                break;
            }
        }

        _logger.log(level, String.format("%s%s%s\u001B[0m", color, moduleText, text));
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
