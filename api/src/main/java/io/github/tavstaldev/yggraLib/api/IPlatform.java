package io.github.tavstaldev.yggraLib.api;

import java.util.Map;

/**
 * Represents a platform-specific setup layer for YggraLib.
 * Implementations register services that work on a specific Minecraft version or server type.
 */
public interface IPlatform {

    /**
     * Sets up platform services for the given plugin.
     *
     * @param plugin            the plugin instance
     * @param serviceCollection the map where services can be registered
     */
    void setup(IYggraPlugin plugin, Map<Class<?>, Object> serviceCollection);
}
