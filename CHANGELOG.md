# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

[Unreleased]: https://github.com/TavstalDev/yggraLib/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/TavstalDev/yggraLib/releases/tag/v0.1.0
