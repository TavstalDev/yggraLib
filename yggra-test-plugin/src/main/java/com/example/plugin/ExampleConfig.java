package com.example.plugin;

import io.github.tavstaldev.yggraLib.bootstrap.YggraConfig;

public class ExampleConfig extends YggraConfig {
    public String locale;
    public boolean usePlayerLocale;
    public boolean checkUpdates;
    public boolean debugMode;
    public String prefix;

    public ExampleConfig(ExamplePlugin plugin) {
        super(plugin, "config.yml", null);
    }

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
