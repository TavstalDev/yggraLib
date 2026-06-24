package io.github.tavstaldev.yggra.core.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class IOUtil {
    public static boolean isDirectoryEmpty(Path dirPath){
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
