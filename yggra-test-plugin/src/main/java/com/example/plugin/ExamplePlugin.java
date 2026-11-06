package com.example.plugin;

import io.github.tavstaldev.yggra.core.YggraPlugin;

public class ExamplePlugin extends YggraPlugin<ExampleConfig> {
    public static ExamplePlugin instance;


    @Override
    public void onPrePluginEnable() {
        instance = this;
        _config = new ExampleConfig(this);
        _config.load();
        logger().info(String.format("Loading %s...", getName()));
    }

    @Override
    public void onPluginEnable() {
        logger().ok(String.format("%s has been successfully loaded.", getName()));
    }

    @Override
    public void onPluginDisable() {
        logger().info(String.format("%s has been successfully unloaded.", getName()));
    }
}
