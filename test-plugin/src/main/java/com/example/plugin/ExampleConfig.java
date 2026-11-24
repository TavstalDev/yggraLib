package com.example.plugin;

import com.github.TavstalDev.yggraLib.bootstrap.YggraConfig;

/**
 * Example configuration implementation for the test plugin.
 */
public class ExampleConfig extends YggraConfig {
    /**
     * The default language code used by the plugin.
     */
    public String locale;

    /**
     * Whether the plugin should prefer the player's language when available.
     */
    public boolean usePlayerLocale;

    /**
     * Whether the plugin should check for updates.
     */
    public boolean checkUpdates;

    /**
     * Whether debug logging is enabled.
     */
    public boolean debugMode;

    /**
     * The prefix used in plugin messages.
     */
    public String prefix;

    /**
     * Creates a new example configuration.
     *
     * @param plugin the plugin instance that owns this configuration
     */
    public ExampleConfig(ExamplePlugin plugin) {
        super(plugin, "config.yml", null);
    }

    /** {@inheritDoc} */
    @Override
    protected void loadDefaults() {
        locale = this.resolveGet("general.locale", "eng");
        this.resolveComment("general.locale", "Default localization of the plugin.");
        usePlayerLocale = this.resolveGet("general.use-player-locale", true);
        this.resolveComment("general.use-player-locale", "Prioritize the player's selected language.");
        checkUpdates = this.resolveGet("general.check-updates", true);
        debugMode = this.resolveGet("general.debug-mode", false);
        prefix = this.resolveGet("general.prefix", "&3Example &8»");
    }
}
