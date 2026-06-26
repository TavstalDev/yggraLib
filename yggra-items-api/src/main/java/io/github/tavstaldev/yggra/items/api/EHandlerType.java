package io.github.tavstaldev.yggra.items.api;

/**
 * Enum representing the various types of item metadata that can be serialized/deserialized.
 */
public enum EHandlerType {
    /** Written book meta: title, author, and pages (Stored as Adventure {@code Component}). */
    BOOK,
    /** Crossbow meta: charged projectiles loaded into the crossbow. */
    CROSSBOW,
    /** Applied enchantments on any item (e.g., Sharpness on a sword). */
    ENCHANTMENT,
    /** Stored enchantments on an enchanted book. */
    ENCHANTMENT_STORAGE,
    /** Single firework effect (firework star): type, flicker, trail, colors, fade colors. */
    FIREWORK_EFFECT,
    /** Firework rocket meta: list of effects and flight power/duration. */
    FIREWORK,
    /** Leather armor dye color. */
    LEATHER_ARMOR,
    /** Potion meta: custom potion name, color, base type, and custom effects. */
    POTION,
    /** Player skull meta: owning player UUID and optional texture/skin profile. */
    SKULL,
    /** Spawn egg meta: custom spawned entity type. */
    SPAWN_EGG
}
