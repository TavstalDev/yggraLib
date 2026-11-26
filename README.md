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
- **Plugin base class** - Extend `YggraPlugin` to get a service container, a logger, a configuration, update checks, runtime library loading, and automatic cleanup on disable.
- **Runtime dependency loading** - Common libraries such as Adventure, triumph-gui, fastboard, Caffeine, SnakeYAML, and XSeries are loaded with [Libby](https://github.com/Byteflux/libby), so you do not need to shade all of them yourself.
- **YAML configuration** - `YggraConfig` handles default values, comments, loading, and saving.
- **Localization** - Translate chat messages and lists per player or for the whole server.
- **Commands** - `CommandBase` and `SubCommand` add permission checks and paginated help menus.
- **Inventory menus** - Build YAML-based menus with `GuiBase`, `GuiButton`, `IGuiService`, and dynamic slot parsing.
- **Item helpers** - Serialize item stacks and handle version-specific item metadata, custom model data, potions, skulls, durability, persistent data, and optional NBT through `INbtAccessor`.
- **Scheduler abstraction** - Run tasks on the main thread, on a region or entity context, or asynchronously. Folia is supported on 1.20.4+ implementations; older versions use the Paper scheduler.
- **Chat and UI services** - Send rich messages, scoreboards, sounds, action bars, boss bars, titles, and particles through service interfaces.
- **Caching and database helpers** - Use memory or Redis caches and repository classes for SQLite, MySQL, and PostgreSQL. Registered caches and databases are cleaned up automatically on disable.
- **Update checks and metrics** - Compare versions semantically, check for new releases, and enable bStats with a few lines of code.

## Supported versions

| Minecraft version | Selected implementation |
| --- | --- |
| 1.8.x | `impl_v1.8.8` |
| 1.9.x - 1.11.x | `impl_v1.9.4` |
| 1.12.x | `impl_v1.12` |
| 1.13.x | `impl_v1.13` |
| 1.14.x - 1.19.x | `impl_v1.14` |
| 1.20.x | `impl_v1.20.4` |
| 1.21.x (before 1.21.5) | `impl_v1.21` |
| 1.21.5 and newer | `impl_v1.21.5` |

`impl_old`, `impl_legacy`, and `impl_modern` hold shared code for older, transitional, and newer versions. `YggraPlugin` selects the implementation automatically at startup using a semantic version check. Newer server versions usually use the newest implementation, but always test your plugin on the versions you plan to support.

## Requirements

- A Bukkit-based server (Spigot, Paper, or a compatible fork).
- A Java version supported by your server. Legacy servers may run on Java 8, while Minecraft 1.21+ servers need Java 21 or newer.
- JDK 8 and JDK 25 for building from source. The library modules target Java 8, 17, or 21 depending on the version implementation. The included Gradle wrapper uses Gradle 9.8.2 with the Foojay toolchain resolver.

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
bootstrap/build/libs/yggraLib-0.1.1.jar
```

This jar contains the API, Bukkit helpers, bootstrap code, and all version implementations. It is meant to be shaded into your plugin or loaded as a local library. The build also produces `yggraLib-0.1.1-sources.jar` and `yggraLib-0.1.1-javadoc.jar`.

## Add yggraLib to your plugin

### From JitPack (recommended)

JitPack publishes the shaded `bootstrap` jar with attached sources and Javadoc artifacts. The main jar contains the API, Bukkit helpers, the runtime, and every version implementation. Add JitPack to your repositories and depend on a release tag (`v0.1.1` for this release):

Gradle (Kotlin DSL):

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.TavstalDev:yggraLib:v0.1.1")
}
```

Gradle (Groovy DSL):

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.TavstalDev:yggraLib:v0.1.1'
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
        <version>v0.1.1</version>
    </dependency>
</dependencies>
```

Shade this dependency into your plugin jar, or mark it as `compileOnly` and load the jar as a server library instead. JitPack also accepts a branch name (for example `master-SNAPSHOT`) or a commit hash as the version.

XSeries 14.0.0 is loaded at runtime through Libby and relocated under `<yggraLib-root-package>.shadow.XSeries` (by default `com.github.TavstalDev.yggraLib.shadow.XSeries`). If you use APIs such as Adventure, Spigot, triumph-gui, or SnakeYAML in your own code, add them as `compileOnly` dependencies. Compile-time examples are in [`test-plugin/build.gradle.kts`](test-plugin/build.gradle.kts), and current dependency versions are in [`gradle.properties`](gradle.properties).

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
    compileOnly(files("libs/yggraLib-0.1.1.jar"))
}
```

## Quick start

### 1. Create your plugin class

Extend `YggraPlugin` and use the `onPrePluginEnable`, `onPluginEnable`, `onPrePluginDisable`, and `onPluginDisable` hooks. `onEnable()` and `onDisable()` are final; returning `false` from `onPrePluginEnable()` skips platform setup and `onPluginEnable()`:

```java
package com.example.plugin;

import com.github.TavstalDev.yggraLib.bootstrap.YggraPlugin;

public class ExamplePlugin extends YggraPlugin<ExampleConfig> {

    @Override
    public boolean onPrePluginEnable() {
        _config = new ExampleConfig(this);
        _config.load();
        return true;
    }

    @Override
    public void onPluginEnable() {
        logger().info("ExamplePlugin is ready.");
    }

    @Override
    public void onPrePluginDisable() {
        // Runs before registered services are cleaned up.
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

`YggraPlugin` loads its runtime libraries, including XSeries, when the server enables the plugin. Runtime relocation targets are derived from the package containing `YggraPlugin`, so relocating yggraLib also changes the runtime library prefixes. The server needs internet access on first start, unless the libraries are already available.

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

Most services are registered by the version-specific platform during startup. `ICache`, `IDatabase`, `IGuiService`, and `INbtAccessor` are optional and must be created and registered by your plugin if you want service lookups and automatic disable cleanup. Use `getRequiredService` when a service must exist, or `getService` when it is optional:

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
| `IGuiService` | Registers, opens, closes, and refreshes menus; provided by your plugin via `GuiService`. |
| `IItemStackAccessor` | Reads and changes item stack values, including durability. |
| `IItemSerializer` | Converts item stacks to and from maps, YAML, and bytes. |
| `INbtAccessor` | Provides optional typed item NBT access; platforms detect a registered implementation at startup. |

`IItemSerializer` is the public serializer service; the implementation is `ItemSerializer`. Custom meta handlers receive the serializer and original `ItemStack` during serialization and deserialization. `INbtAccessor` is opt-in and no implementation ships with yggraLib; register one before platform setup if you need NBT in serialized items.

## Commands

Extend `CommandBase` and implement `onCommand` and `onTabComplete`. Create `SubCommand` entries to add permission checks and help lines. The help menu is paginated and can show clickable previous and next buttons.

## Menus

Extend `GuiBase` to describe a menu. yggraLib creates a YAML file for the menu, resolves dynamic slots, and applies buttons; `GuiBase` and `GuiButton` use `IItemSerializer`. `GuiService` implements `IGuiService`: register it under that interface so `YggraPlugin.onDisable()` can close menus, call `unregisterAll()`, and invalidate cached menus automatically. Its listeners are registered lazily and re-registered after a plugin reload.

## Database and cache

The `api` module includes building blocks for optional storage:

- `ICache` with `MemoryCache` and `RedisCache` implementations; `evictAll()` clears all entries. For `RedisCache`, this means the keys tracked by that cache instance, not a server-wide Redis flush.
- `IDatabase` with HikariCP-based database helpers.
- `IRepository` and `RepositoryBase` with SQLite, MySQL, and PostgreSQL implementations. SQLite falls back to select-then-delete on versions older than 3.35, where `RETURNING` is unavailable.

Register or create these components in your plugin when you need them. They are not added to the service container automatically. If an `ICache` or `IDatabase` is registered, `YggraPlugin.onDisable()` calls `evictAll()` or `unload()` before `onPluginDisable()`.

## Update checks and metrics

- `isUpToDate()` reads the JSON from `downloadUrl()` and compares its `tag_name` with your plugin version component by component. Leading `v` prefixes and suffixes such as `-SNAPSHOT` are accepted. The result is `true` when the current version is at least the latest release, `false` when a newer release exists, and `null` when the check cannot be completed.
- To enable bStats, pass your bStats plugin ID to the `YggraPlugin` constructor. Overloads also accept custom libraries and Maven repositories. Pass `null` as the ID to disable metrics.

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