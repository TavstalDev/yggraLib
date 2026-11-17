package com.github.TavstalDev.yggraLib.api.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility methods for basic file and directory operations.
 */
@SuppressWarnings("unused")
public class IOUtil {

    /**
     * Checks whether a directory is empty.
     *
     * @param dirPath the directory path to check
     * @return {@code true} if the directory does not exist or has no entries;
     *         {@code false} if it contains entries or cannot be read
     */
    public static boolean isDirectoryEmpty(Path dirPath) {
        if (Files.notExists(dirPath)) {
            return true;
        }

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dirPath)) {
            return !directoryStream.iterator().hasNext();
        } catch (IOException ex) {
            return false;
        }
    }
}
