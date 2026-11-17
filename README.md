# yggraLib

![Release (latest by date)](https://img.shields.io/github/v/release/TavstalDev/yggraLib?style=plastic-square)
![Workflow Status](https://img.shields.io/github/actions/workflow/status/TavstalDev/yggraLib/release.yml?branch=stable&label=build&style=plastic-square)
![License](https://img.shields.io/github/license/TavstalDev/yggraLib?style=plastic-square)
![Downloads](https://img.shields.io/github/downloads/TavstalDev/yggraLib/total?style=plastic-square)
![Stars](https://img.shields.io/github/stars/TavstalDev/yggraLib?style=plastic-square)
![Issues](https://img.shields.io/github/issues/TavstalDev/yggraLib?style=plastic-square)
![Forks](https://img.shields.io/github/forks/TavstalDev/yggraLib?style=plastic-square)

A library for building Minecraft plugins that work across many server versions.

yggraLib gives you one shared API for the things most plugins need: configuration, messages and translations, commands, menus, items, scheduling, scoreboards, sounds, caching, and databases. It picks the right version-specific implementation when your plugin starts, so your code can stay the same from Minecraft 1.8.8 to 26.x.

## Features

- **One codebase for many versions** - Write your plugin once and let yggraLib choose the correct implementation for the running server.
- **Plugin base class** - Extend `YggraPlugin` to get a service container, a logger, a configuration, update checks, and runtime library loading.
- **Runtime dependency loading** - Common libraries such as Adventure, triumph-gui, fastboard, Caffeine, and SnakeYAML are loaded with [Libby](https://github.com/Byteflux/libby), so you do not need to shade all of them yourself.
- **YAML configuration** - `YggraConfig` handles default values, comments, loading, and saving.
- **Localization** - Translate chat messages and lists per player or for the whole server.
- **Commands** - `CommandBase` and `SubCommand` add permission checks and paginated help menus.
- **Inventory menus** - Build YAML-based menus with `GuiBase`, `GuiButton`, `GuiService`, and dynamic slot parsing.
- **Item helpers** - Serialize item stacks and handle version-specific item metadata, custom model data, potions, and skulls.
- **Scheduler abstraction** - Run tasks on the main thread, on a region or entity context, or asynchronously. Folia is supported when the server runs Folia.
- **Chat and UI services** - Send rich messages, scoreboards, sounds, action bars, boss bars, titles, and particles through service interfaces.
- **Caching and database helpers** - Use memory or Redis caches and repository classes for SQLite, MySQL, and PostgreSQL.
- **Update checks and metrics** - Check for new releases and enable bStats with a few lines of code.

## Supported versions

| Minecraft version | Selected implementation |
| --- | --- |
| 1.8.x - 1.11.x | `impl_v1.8.8` |
| 1.12.x | `impl_v1.12` |
| 1.13.x | `impl_v1.13` |
| 1.14.x - 1.19.x | `impl_v1.14` |
| 1.20.x | `impl_v1.20.4` |
| 1.21.x (before 1.21.5) | `impl_v1.21` |
| 1.21.5 and newer | `impl_v1.21.5` |

`impl_legacy` and `impl_modern` hold shared code for old and new versions. `YggraPlugin` selects the implementation automatically at startup. Newer server versions usually use the newest implementation, but always test your plugin on the versions you plan to support.

## Requirements

- A Bukkit-based server (Spigot, Paper, or a compatible fork).
- A Java version supported by your server. Legacy servers may run on Java 8, while Minecraft 1.21+ servers need Java 21 or newer.
- JDK 8 and JDK 25 for building from source. The library modules target Java 8, and the build uses newer toolchains. The included Gradle wrapper uses Gradle 9.8.2.

## Project structure

| Module | Purpose |
| --- | --- |
| `api` | Platform-independent interfaces, models, caching, database, scheduler, and utilities. |
| `bukkit` | Bukkit-specific commands, GUI helpers, item handling, and services. |
| `bootstrap` | The plugin base class, configuration base, and the shaded library jar. |
| `implementation/*` | Version-specific services and item handlers. |
| `test-plugin` | A small example plugin that shows how to use the library. |

## Build from source

You only need this when you want to build the library yourself. To consume a released version, use JitPack instead.  

Clone the repository and run the Gradle wrapper:

```bash
git clone https://github.com/TavstalDev/yggraLib.git
cd yggraLib
./gradlew build
```

The main library jar is created by the `bootstrap` module:

```bash
./gradlew :bootstrap:shadowJar
```

You can find it at:

```text
bootstrap/build/libs/yggraLib-0.1.0.jar
```

This jar contains the API, Bukkit helpers, bootstrap code, and all version implementations. It is meant to be shaded into your plugin or loaded as a local library.

## Add yggraLib to your plugin

### From JitPack (recommended)

JitPack publishes the shaded `bootstrap` jar as a single artifact that contains the API, Bukkit helpers, the runtime, and every version implementation. Add JitPack to your repositories and depend on a release tag:

Gradle (Kotlin DSL):

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.TavstalDev:yggraLib:<version>")
}
```

Gradle (Groovy DSL):

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.TavstalDev:yggraLib:<version>'
}
```

Maven:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.TavstalDev</groupId>
        <artifactId>yggraLib</artifactId>
        <version>replace-with-version</version>
    </dependency>
</dependencies>
```

Shade this dependency into your plugin jar, or mark it as `compileOnly` and load the jar as a server library instead. JitPack also accepts a branch name (for example `master-SNAPSHOT`) or a commit hash as the version.

The shaded jar deliberately leaves third-party APIs out. If you use Adventure types in your own code, also add the Adventure API and its serializers as `compileOnly` dependencies. The full list, including Spigot, triumph-gui, and fastboard, is available in [`test-plugin/build.gradle.kts`](test-plugin/build.gradle.kts).

### From source (multi-project)

The `test-plugin` module is the best complete example. It uses the library as a multi-project dependency:

```kotlin
dependencies {
    // These are provided at compile time by the library jar.
    compileOnly(project(":api"))
    compileOnly(project(":bukkit"))

    // The bootstrap jar adds the runtime and all version implementations.
    implementation(project(path = ":bootstrap", configuration = "shadow")) {
        isTransitive = false
    }
}
```

### Local jar

If your plugin is a separate build, first build the library jar, copy it into your plugin's `libs` folder, and depend on it as a local file:

```kotlin
dependencies {
    compileOnly(files("libs/yggraLib-0.1.0.jar"))
}
```

## Quick start

### 1. Create your plugin class

Extend `YggraPlugin` and use the `onPrePluginEnable`, `onPluginEnable`, and `onPluginDisable` hooks:

```java
package com.example.plugin;

import com.github.TavstalDev.yggraLib.bootstrap.YggraPlugin;

public class ExamplePlugin extends YggraPlugin<ExampleConfig> {

    @Override
    public void onPrePluginEnable() {
        _config = new ExampleConfig(this);
        _config.load();
    }

    @Override
    public void onPluginEnable() {
        logger().ok("ExamplePlugin is ready.");
    }

    @Override
    public void onPluginDisable() {
        logger().info("ExamplePlugin has stopped.");
    }

    @Override
    public String downloadUrl() {
        // Return a JSON endpoint that contains a "tag_name" field.
        return "https://api.github.com/repos/you/your-plugin/releases/latest";
    }
}
```

`YggraPlugin` loads its runtime libraries when the server enables the plugin. The server needs internet access on first start, unless the libraries are already available.

### 2. Set the main class

Point your `plugin.yml` to your plugin class:

```yaml
name: Example
main: com.example.plugin.ExamplePlugin
version: '1.0.0'
# Use the oldest server version you support.
api-version: '1.13'
```

### 3. Create a configuration

Extend `YggraConfig` and resolve the values you need:

```java
package com.example.plugin;

import com.github.TavstalDev.yggraLib.bootstrap.YggraConfig;

public class ExampleConfig extends YggraConfig {

    public String prefix;
    public boolean debugMode;

    public ExampleConfig(ExamplePlugin plugin) {
        super(plugin, "config.yml", null);
    }

    @Override
    protected void loadDefaults() {
        prefix = resolveGet("general.prefix", "&3Example &8»");
        resolveComment("general.prefix", "The prefix used in plugin messages.");

        debugMode = resolveGet("general.debug-mode", false);
        resolveComment("general.debug-mode", "Enable extra debug messages.");
    }
}
```

`resolveGet` writes a default value when the path is missing. `resolveComment` adds a comment without overwriting an existing one.

## Services

Services are registered by the version-specific platform during startup. Use `getRequiredService` when a service must exist, or `getService` when it is optional:

```java
try {
    IChatService chat = getRequiredService(IChatService.class);
    IScheduler scheduler = getRequiredService(IScheduler.class);

    scheduler.runLater(() -> chat.sendRichMsg(player, "<green>Hello!</green>"), 20);
} catch (Exception ex) {
    logger().error("A required service is missing.", ex);
}
```

Common services include:

| Service | What it does |
| --- | --- |
| `IScheduler` | Runs tasks now, later, repeatedly, or asynchronously. |
| `ITranslationService` | Turns language keys into text for the server or a player. |
| `IChatService` | Sends rich, localized, and button-based messages. |
| `ISoundService` | Plays and stops sounds for players or locations. |
| `IScoreboardService` | Shows and updates player scoreboards. |
| `IGuiRenderer` | Builds GUI items from configuration. |
| `IItemStackAccessor` | Reads and changes item stack values. |
| `ItemSerializerBase` | Converts item stacks to and from maps, YAML, and bytes. |

## Commands

Extend `CommandBase` and implement `onCommand` and `onTabComplete`. Create `SubCommand` entries to add permission checks and help lines. The help menu is paginated and can show clickable previous and next buttons.

## Menus

Extend `GuiBase` to describe a menu. yggraLib creates a YAML file for the menu, resolves dynamic slots, and applies buttons. Use `GuiService` to register, open, close, and refresh menus for players.

## Database and cache

The `api` module includes building blocks for optional storage:

- `ICache` with `MemoryCache` and `RedisCache` implementations.
- `IDatabase` with HikariCP-based database helpers.
- `IRepository` and `RepositoryBase` with SQLite, MySQL, and PostgreSQL implementations.

Register or create these components in your plugin when you need them. They are not added to the service container automatically.

## Update checks and metrics

- `isUpToDate()` reads the JSON from `downloadUrl()` and compares its `tag_name` with your plugin version. Return `null` when the check cannot be completed.
- To enable bStats, pass your bStats plugin ID to the `YggraPlugin` constructor. Pass `null` to disable metrics.

## Contributing

1. Fork the repository.
2. Create a branch for your change.
3. Make your changes and run `./gradlew build`.
4. Open a pull request with a short description.

Please keep changes focused and add tests or examples when they help explain a new feature.

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for more details.

## Contact

For issues or feature requests, use the
[GitHub issue tracker](https://github.com/TavstalDev/yggraLib/issues).