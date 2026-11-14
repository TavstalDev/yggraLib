package io.github.tavstaldev.yggraLib.bootstrap.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LibraryInfo {
    public @NotNull String groupId;
    public @NotNull String artifactId;
    public @NotNull String version;

    public @Nullable LibraryRelocationInfo relocationInfo;

    public LibraryInfo(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @Nullable LibraryRelocationInfo relocationInfo) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.relocationInfo = relocationInfo;
    }

    public LibraryInfo(@NotNull String groupId, @NotNull String artifactId, @NotNull String version,
                       @NotNull String relocationPattern, @NotNull String relocatedPattern) {
        this(groupId, artifactId, version, new LibraryRelocationInfo(relocationPattern, relocatedPattern));
    }
}
