package com.github.TavstalDev.yggraLib.bukkit.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Method;

/**
 * Utility class for handling server version-related operations.
 */
@SuppressWarnings("unused")
public class VersionUtils {
    private static String _version = null;

    /**
     * Retrieves the current server version as a string.
     *
     * @return The server version string, e.g., "1.8.8-R0.1-SNAPSHOT".
     */
    public static String getServerVersion() {
        if (_version != null) {
            return _version;
        }

        String version;
        try {
            Method method = Bukkit.class.getDeclaredMethod("getMinecraftVersion");
            version = (String) method.invoke(null);
        } catch (Exception e) {
            // Fallback for older versions
            version = Bukkit.getServer().getClass().getPackage().getName()
                    .split("\\.")[3]
                    .replace("_", ".")
                    .replaceAll("[A-Z]|[a-z]", "");
        }
        _version = version;
        return version;
    }

    /**
     * Parses a version string into its major, minor, and patch components.
     * The input is trimmed; an optional leading "v" and any suffix starting with "-"
     * are removed, and underscores are treated as separators. A missing patch
     * component defaults to 0.
     *
     * @param version The version string to parse, e.g., "v1.16.5" or "1.8.8-R0.1-SNAPSHOT".
     * @return An array of three integers containing the major, minor, and patch versions.
     * @throws NumberFormatException If a version component is not a valid integer.
     */
    public static int[] parse(String version) {
        String ver = version.trim();
        if (ver.startsWith("v") || ver.startsWith("V"))
            ver = ver.substring(1);
        int suffix = ver.indexOf('-');
        if (suffix >= 0)
            ver = ver.substring(0, suffix);
        ver = ver.replace('_', '.');
        String[] parts = ver.split("\\.", 3);
        int[] result = new int[3];
        result[0] = Integer.parseInt(parts[0]);
        result[1] = Integer.parseInt(parts[1]);
        if (parts.length == 3)
            result[2] = Integer.parseInt(parts[2]);
        else
            result[2] = 0;
        return result;
    }

    /**
     * Checks whether the first version is newer than the second version.
     * Versions are compared component by component (major, then minor, then patch).
     *
     * @param versionA The first version string to compare.
     * @param versionB The second version string to compare.
     * @return {@code true} if versionA is newer than versionB, otherwise {@code false}.
     */
    public static boolean isANewer(String versionA, String versionB) {
        int[] a = parse(versionA);
        int majA = a[0];
        int minA = a[1];
        int patA = a[2];
        int[] b = parse(versionB);
        int majB = b[0];
        int minB = b[1];
        int patB = b[2];
        return (majA > majB ||
                (majA == majB && minA > minB) ||
                (majA == majB && minA == minB && patA > patB));
    }

    /**
     * Checks if the server version is at least the specified major and minor version.
     *
     * @param major The major version to compare against.
     * @param minor The minor version to compare against.
     * @return True if the server version is at least the specified version, false otherwise.
     */
    public static boolean isAtLeast(int major, int minor) {
        return isAtLeast(major, minor, 0);
    }

    /**
     * Checks if the server version is at least the specified major, minor, and patch version.
     * This method parses the server version string and compares it against the provided version numbers.
     * If the patch version is not specified in the server version, it defaults to 0.
     *
     * @param major The major version to compare against.
     * @param minor The minor version to compare against.
     * @param patch The patch version to compare against.
     * @return True if the server version is at least the specified version, false otherwise.
     */
    public static boolean isAtLeast(int major, int minor, int patch) {
        String[] parts = getServerVersion().split("\\.");
        int maj = Integer.parseInt(parts[0]);
        int min = Integer.parseInt(parts[1]);
        int pat;
        if (parts.length > 2) {
            pat = Integer.parseInt(parts[2]);
        } else {
            pat = 0; // Default to 0 if patch version is not specified
        }
        return maj > major || maj == major && min > minor || maj == major && min == minor && pat >= patch;
    }

    /**
     * Determines if the server version is considered legacy.
     *
     * @return True if the server version is legacy (not at least 1.13), false otherwise.
     */
    public static boolean isLegacy() {
        return !isAtLeast(1, 13); // Legacy if not at least 1.13
    }

    /**
     * Checks whether the server is running on Folia.
     *
     * @return {@code true} if Folia classes are available, otherwise {@code false}
     */
    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    /**
     * Checks whether the server is running on Paper or a Paper-based fork.
     *
     * @return {@code true} if Paper classes are available, otherwise {@code false}
     */
    public static boolean isPaper() {
        try {
            // Modern Paper (1.20+) uses ServerBuildInfo
            Class.forName("io.papermc.paper.ServerBuildInfo");
            return true;
        } catch (ClassNotFoundException e1) {
            try {
                // Older Paper versions use PaperConfig
                Class.forName("com.destroystokyo.paper.PaperConfig");
                return true;
            } catch (ClassNotFoundException e2) {
                return false;
            }
        }
    }
}