package com.github.TavstalDev.yggraLib.bootstrap.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the information needed to load an external library.
 */
public class LibraryInfo {
    /**
     * The group ID of the library.
     */
    public @NotNull String groupId;

    /**
     * The artifact ID of the library.
     */
    public @NotNull String artifactId;

    /**
     * The version of the library.
     */
    public @NotNull String version;

    /**
     * Optional relocation information for the library.
     */
    public @Nullable LibraryRelocationInfo relocationInfo;

    /**
     * Creates a new library information object.
     *
     * @param groupId the group ID of the library
     * @param artifactId the artifact ID of the library
     * @param version the version of the library
     * @param relocationInfo optional relocation information, or {@code null} if not needed
     */
    public LibraryInfo(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @Nullable LibraryRelocationInfo relocationInfo) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.relocationInfo = relocationInfo;
    }

    /**
     * Creates a new library information object with relocation details.
     *
     * @param groupId the group ID of the library
     * @param artifactId the artifact ID of the library
     * @param version the version of the library
     * @param relocationPattern the original package pattern
     * @param relocatedPattern the new package pattern
     */
    public LibraryInfo(@NotNull String groupId, @NotNull String artifactId, @NotNull String version,
                       @NotNull String relocationPattern, @NotNull String relocatedPattern) {
        this(groupId, artifactId, version, new LibraryRelocationInfo(relocationPattern, relocatedPattern));
    }
}
