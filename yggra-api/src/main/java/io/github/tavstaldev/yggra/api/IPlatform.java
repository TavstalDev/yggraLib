package io.github.tavstaldev.yggra.api;

import java.util.Map;

public interface IPlatform {
    void setup(IYggraPlugin plugin, Map<Class<?>, Object> serviceCollection);
}
