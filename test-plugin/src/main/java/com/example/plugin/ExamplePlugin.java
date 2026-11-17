package com.example.plugin;

import com.github.TavstalDev.yggraLib.bootstrap.YggraPlugin;

/**
 * Example plugin implementation used for testing the bootstrap module.
 */
public class ExamplePlugin extends YggraPlugin<ExampleConfig> {
    /**
     * The current plugin instance.
     */
    public static ExamplePlugin instance;

    /**
     * Runs before the main plugin setup.
     * <p>
     * This method stores the plugin instance and loads the configuration.
     */
    @Override
    public void onPrePluginEnable() {
        instance = this;
        logger().info(String.format("Loading %s...", getName()));
        _config = new ExampleConfig(this);
        _config.load();
    }

    /**
     * Runs after the main plugin setup is complete.
     */
    @Override
    public void onPluginEnable() {
        logger().ok(String.format("%s has been successfully loaded.", getName()));
    }

    /**
     * Runs when the plugin is being disabled.
     */
    @Override
    public void onPluginDisable() {
        logger().info(String.format("%s has been successfully unloaded.", getName()));
    }

    /**
     * Returns the URL used to check for plugin updates.
     *
     * @return the download or update URL, or an empty string if none is available
     */
    @Override
    public String downloadUrl() {
        return "";
    }
}
