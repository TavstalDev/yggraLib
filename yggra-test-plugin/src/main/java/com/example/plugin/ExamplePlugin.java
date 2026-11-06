package com.example.plugin;

import io.github.tavstaldev.yggra.core.YggraPlugin;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;

public class ExamplePlugin extends YggraPlugin<ExampleConfig> {
    public static ExamplePlugin instance;
    private BukkitLibraryManager libraryManager;

    @Override
    public void onPrePluginEnable() {
        instance = this;
        logger().info(String.format("Loading %s...", getName()));
        this.libraryManager = new BukkitLibraryManager(this);
        libraryManager.addMavenCentral();
        libraryManager.addSonatype();
        libraryManager.addRepository("https://jitpack.io");
        loadDependencies();
        _config = new ExampleConfig(this);
        _config.load();
    }

    @Override
    public void onPluginEnable() {
        logger().ok(String.format("%s has been successfully loaded.", getName()));
    }

    @Override
    public void onPluginDisable() {
        logger().info(String.format("%s has been successfully unloaded.", getName()));
    }

    private void loadDependencies() {

        // org.yaml:snakeyaml - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("org{}yaml")
                .artifactId("snakeyaml")
                .version("2.7")
                .relocate("org{}yaml", "com.example.plugin.shadow.yaml")
                .build());

        // org.bspfsystems:yamlconfiguration - Required for 1.8.8 support
        /*libraryManager.loadLibrary(Library.builder()
                .groupId("org{}bspfsystems")
                .artifactId("yamlconfiguration")
                .version("2.0.2")
                .relocate("org{}bspfsystems", "com.example.plugin.shadow.bspfsystems")
                .build());*/

        // dev.triumphteam:triumph-gui - Required for GUI support
        libraryManager.loadLibrary(Library.builder()
                .groupId("dev{}triumphteam")
                .artifactId("triumph-gui")
                .version("3.1.13")
                .relocate("dev{}triumphteam", "com.example.plugin.shadow.triumphteam")
                .build());

        // fr.mrmicky:fastboard - Required for scoreboard support
        libraryManager.loadLibrary(Library.builder()
                .groupId("fr{}mrmicky")
                .artifactId("fastboard")
                .version("2.2.0")
                .relocate("fr{}mrmicky", "com.example.plugin.shadow.mrmicky")
                .build());

        //#region Adventure-API

        // net.kyori:examination-api
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("examination-api")
                .version("1.3.0")
                .relocate("net{}kyori", "com.example.plugin.shadow.kyori")
                .build());

        // net.kyori:examination-string
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("examination-string")
                .version("1.3.0")
                .relocate("net{}kyori", "com.example.plugin.shadow.kyori")
                .build());

        // net.kyori:adventure-key
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-key")
                .version("4.26.1")
                .relocate("net{}kyori", "com.example.plugin.shadow.kyori")
                .build());

        // net.kyori:adventure-api - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-api")
                .version("4.26.1")
                .relocate("net{}kyori", "com.example.plugin.shadow.kyori")
                .build());

        // net.kyori:adventure-text-serializer-legacy - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-legacy")
                .version("4.26.1")
                .relocate("net{}kyori", "com.example.plugin.shadow.kyori")
                .build());

        // net.kyori:adventure-text-minimessage - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-minimessage")
                .version("4.26.1")
                .relocate("net{}kyori", "com.example.plugin.shadow.kyori")
                .build());

        // net.kyori:adventure-text-serializer-plain - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-plain")
                .version("4.26.1")
                .relocate("net{}kyori", "com.example.plugin.shadow.kyori")
                .build());

        // net.kyori:adventure-text-serializer-gson - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-gson")
                .version("4.26.1")
                .relocate("net{}kyori", "com.example.plugin.shadow.kyori")
                .build());

        //#endregion
    }
}
