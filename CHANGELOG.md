# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.1] - 2026-11-26

Expands multi-version support and refines the plugin lifecycle, item serialization, and optional service APIs.

### Added

- **Minecraft 1.9.4 support** - New `impl_v1.9.4` platform and version-specific potion metadata handler for 1.9.x through 1.11.x servers.
- **Shared legacy module** - New `impl_old` module for common 1.8-1.14 code, with `impl_legacy` retained for transitional 1.12-1.14 services.
- **Cache eviction** - `ICache.evictAll()` for clearing all cached entries; `RedisCache` removes only keys tracked by that cache instance.
- **Plugin lifecycle hooks** - `onPrePluginEnable()` can now cancel startup by returning `false`, and `onPrePluginDisable()` runs before automatic cleanup.
- **Automatic shutdown cleanup** - Registered caches are evicted, databases unloaded, boss bars cleared, scoreboards hidden, and registered GUIs closed and unregistered during disable.
- **GUI service API** - `IGuiService` with `unregisterAll()`, lazy listener registration, and listener re-registration after plugin reload.
- **Item APIs** - Public `IItemSerializer`, optional `INbtAccessor`, extracted `IPersistentDataMapper`, `NbtUtils.set(...)`, and item durability accessors.
- **Version utilities** - `VersionUtils.parse(String)`, `VersionUtils.isANewer(String, String)`, and patch-aware `isAtLeast(int, int, int)`.
- **XSeries integration** - XSeries 14.0.0 is available as a compile-only dependency, loaded at runtime with Libby, and relocated to avoid conflicts.
- **Published documentation artifacts** - Sources and Javadoc jars are attached to the published `bootstrap` artifact.

### Changed

- **Version selection** - Platform selection uses semantic version comparison; 1.9-1.11 now use `impl_v1.9.4`, and the 1.21.5 cutoff is patch-aware.
- **Item serialization** - `ItemSerializerBase` and per-version serializers were consolidated into `ItemSerializer implements IItemSerializer`, with XMaterial-based lookups, durability support, version-aware potion metadata, and meta handlers that receive the original `ItemStack`.
- **YAML configuration handling** - Meta handlers use `org.bspfsystems.yamlconfiguration` `ConfigurationSection` instead of Bukkit's implementation.
- **Update checks** - Versions are compared component by component, with support for `v` prefixes, suffixes, underscores, and patch versions.
- **Logging** - Module labels are plugin-prefixed, success messages use `FINE`, debug messages use `FINER`, and exceptions retain their stack traces.
- **Runtime relocation** - Library relocation packages are derived from the `YggraPlugin` package instead of a hard-coded prefix.
- **Database deletion** - Delete-by-criteria suffixes are dialect-specific, and SQLite falls back to select-then-delete on versions older than 3.35.
- **Dependencies and tooling** - Bumped yggraActionBar, yggraBossBar, and yggraTitle to 0.1.2 and yggraParticle to 0.1.3; added the Foojay toolchain resolver and manual release workflow dispatch.

### Fixed

- Bootstrap errors now distinguish platform creation failures from service setup failures and preserve exception stack traces.
- Update checks no longer report false negatives for newer patch versions and handle malformed or missing `tag_name` values.
- GUI listeners are re-registered after a plugin reload.
- Restored item NBT is applied after item meta, preventing later meta writes from overwriting it.
- Delete-by-criteria works on SQLite versions that do not support `RETURNING`.

### Removed

- `ItemSerializerBase`, its per-version implementations, redundant version-specific registry classes, and duplicate legacy language/sound classes.
- The untyped `INbtAccessor.set(ItemStack, String, Object)` method; the array API now uses typed primitive arrays and `remove(...)`.

## [0.1.0] - 2026-11-17

Initial release.

### Added

- **Plugin foundation** - `YggraPlugin` base class with a service container, logger, configuration hooks, plugin update checks, bStats metrics, and runtime library loading through Libby.
- **Configuration** - `YggraConfig` for YAML configuration with default values, comments, and load/save handling.
- **Multi-version support** - Automatic selection of version-specific implementations for Minecraft 1.8.x through 1.21.x (`impl_v1.8.8`, `impl_v1.12`, `impl_v1.13`, `impl_v1.14`, `impl_v1.20.4`, `impl_v1.21`, and `impl_v1.21.5`), with shared code in `impl_legacy` and `impl_modern`.
- **Services** - Interfaces and implementations for scheduling (with Folia support), localization, chat and rich messages, sounds, scoreboards, GUI rendering, and item stack access.
- **Commands** - `CommandBase` and `SubCommand` with permission checks and paginated, clickable help menus.
- **Menus** - YAML-based inventories built with `GuiBase`, `GuiButton`, and `GuiService`, including dynamic slot parsing.
- **Items** - Version-specific item serialization, metadata handlers, custom model data, potions, and skull textures.
- **Chat and UI helpers** - Adventure/MiniMessage text parsing plus action bar, boss bar, title, and particle integrations through external yggra libraries.
- **Caching and databases** - `ICache` with `MemoryCache` and `RedisCache`, plus `IDatabase` and `IRepository` with SQLite, MySQL/MariaDB, and PostgreSQL implementations.
- **Utilities** - Helpers for IO, lists, maps, type conversion, colors, versions, and world/location handling.
- **JitPack publishing** - The shaded `bootstrap` jar is published as `com.github.TavstalDev:yggraLib` for easy consumption from GitHub releases.
- **Documentation and example** - Project README, Javadoc, MIT license, and a `test-plugin` module demonstrating the main features.

[Unreleased]: https://github.com/TavstalDev/yggraLib/compare/v0.1.1...HEAD
[0.1.1]: https://github.com/TavstalDev/yggraLib/releases/tag/v0.1.1
[0.1.0]: https://github.com/TavstalDev/yggraLib/releases/tag/v0.1.0
