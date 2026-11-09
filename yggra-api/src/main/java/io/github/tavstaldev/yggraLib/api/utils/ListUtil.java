package io.github.tavstaldev.yggraLib.api.utils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ListUtil {
    public static <T> List<T> of(T value) {
        List<T> result = new ArrayList<>();
        result.add(value);
        return result;
    }

    public static <T> List<T> of(T value1, T value2) {
        List<T> result = new ArrayList<>();
        result.add(value1);
        result.add(value2);
        return result;
    }

    public static <T> List<T> of(T value1, T value2, T value3) {
        List<T> result = new ArrayList<>();
        result.add(value1);
        result.add(value2);
        result.add(value3);
        return result;
    }

    public static <T> List<T> of(T value1, T value2, T value3, T value4) {
        List<T> result = new ArrayList<>();
        result.add(value1);
        result.add(value2);
        result.add(value3);
        result.add(value4);
        return result;
    }

    public static <T> List<T> of(T value1, T value2, T value3, T value4, T value5) {
        List<T> result = new ArrayList<>();
        result.add(value1);
        result.add(value2);
        result.add(value3);
        result.add(value4);
        result.add(value5);
        return result;
    }
}
