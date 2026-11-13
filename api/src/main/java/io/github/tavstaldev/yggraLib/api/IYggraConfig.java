package io.github.tavstaldev.yggraLib.api;

import java.util.List;

public interface IYggraConfig {

    <T> T get(String path, T value) ;

    void resolve(String path, Object value) ;

    <T> T resolveGet(String path, T value) ;

    void resolveComment(String path, String comment);

    void resolveComments(String path, List<String> comments);
}
