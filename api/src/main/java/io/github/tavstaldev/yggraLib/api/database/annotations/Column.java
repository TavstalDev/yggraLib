package io.github.tavstaldev.yggraLib.api.database.annotations;

import io.github.tavstaldev.yggraLib.api.database.enums.EColumnKind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a database column.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * The name of the database column.
     */
    String value();

    /**
     * The constraint kind for the column.
     */
    EColumnKind kind() default EColumnKind.DEFAULT;

    /**
     * The column length, used for VARCHAR types.
     */
    int length() default 255;

    /**
     * Whether the column allows null values.
     */
    boolean nullable() default true;
}