package io.github.tavstaldev.yggraLib.api.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class ListUtil {
    @SafeVarargs
    public static <T> List<T> of(T... values) {
        List<T> result = new ArrayList<>(values.length);
        Collections.addAll(result, values);
        return result;
    }
}
