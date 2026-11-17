package io.github.tavstaldev.yggraLib.api;

import java.util.List;

/**
 * Represents a simple configuration API for reading and writing values.
 * It is used to get values and to make sure default values or comments exist.
 */
public interface IYggraConfig {

    /**
     * Gets a value from the configuration.
     *
     * @param path  the configuration path
     * @param value the default value to return if the path is missing
     * @param <T>   the value type
     * @return the stored value, or the default value if nothing is found
     */
    <T> T get(String path, T value);

    /**
     * Sets a value in the configuration if it is missing or needs to be resolved.
     *
     * @param path  the configuration path
     * @param value the value to set
     */
    void resolve(String path, Object value);

    /**
     * Gets a value from the configuration and resolves a default value if needed.
     *
     * @param path  the configuration path
     * @param value the default value to use
     * @param <T>   the value type
     * @return the resolved or stored value
     */
    <T> T resolveGet(String path, T value);

    /**
     * Adds or updates a comment for a configuration path.
     *
     * @param path    the configuration path
     * @param comment the comment text
     */
    void resolveComment(String path, String comment);

    /**
     * Adds or updates multiple comments for a configuration path.
     *
     * @param path     the configuration path
     * @param comments the comment lines
     */
    void resolveComments(String path, List<String> comments);
}
