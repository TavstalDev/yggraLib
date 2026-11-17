package io.github.tavstaldev.yggraLib.api.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods for creating lists.
 */
@SuppressWarnings("unused")
public class ListUtil {

    /**
     * Creates a new mutable list with the given values.
     *
     * @param values the values to add to the list
     * @param <T>    the element type
     * @return a new list containing the provided values
     */
    @SafeVarargs
    public static <T> List<T> of(T... values) {
        List<T> result = new ArrayList<>(values.length);
        Collections.addAll(result, values);
        return result;
    }
}
