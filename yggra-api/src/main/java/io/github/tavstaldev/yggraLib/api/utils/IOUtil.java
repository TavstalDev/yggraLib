package io.github.tavstaldev.yggraLib.api.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("unused")
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
