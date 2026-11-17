package com.github.TavstalDev.yggraLib.bootstrap.models;

import org.jetbrains.annotations.NotNull;

/**
 * Stores the information needed to relocate a library package.
 */
public class LibraryRelocationInfo {
    /**
     * The original package pattern that should be replaced.
     */
    public @NotNull String pattern;

    /**
     * The new package pattern used after relocation.
     */
    public @NotNull String relocatedPattern;

    /**
     * Creates a new relocation information object.
     *
     * @param pattern the original package pattern
     * @param relocatedPattern the new package pattern
     */
    public LibraryRelocationInfo(@NotNull String pattern, @NotNull String relocatedPattern) {
        this.pattern = pattern;
        this.relocatedPattern = relocatedPattern;
    }
}
