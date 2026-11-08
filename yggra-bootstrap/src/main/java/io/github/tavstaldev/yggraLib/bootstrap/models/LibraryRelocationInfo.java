package io.github.tavstaldev.yggraLib.bootstrap.models;

import org.jetbrains.annotations.NotNull;

public class LibraryRelocationInfo {
    public @NotNull String pattern;
    public @NotNull String relocatedPattern;

    public LibraryRelocationInfo(@NotNull String pattern, @NotNull String relocatedPattern) {
        this.pattern = pattern;
        this.relocatedPattern = relocatedPattern;
    }
}
