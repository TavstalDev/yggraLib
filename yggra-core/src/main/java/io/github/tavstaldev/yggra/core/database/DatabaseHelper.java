package io.github.tavstaldev.yggra.core.database;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Utility methods for mapping Java fields to SQL column types and safely setting field values.
 */
public class DatabaseHelper {

    /**
     * Maps a Java field type to its corresponding SQL column type.
     *
     * @param field The Java field to map.
     * @param columnLength The column length used for VARCHAR types.
     * @return The SQL type string.
     * @throws IllegalArgumentException if the field type is not supported.
     */
    public static String getSqlTypeForField(Field field, int columnLength) {
        return switch (field.getType().getName()) {
            case "java.lang.String" -> String.format("VARCHAR(%s)", columnLength);
            case "int", "java.lang.Integer" -> "INTEGER";
            case "long", "java.lang.Long" -> "BIGINT";
            case "boolean", "java.lang.Boolean" -> "BOOLEAN";
            case "double", "java.lang.Double", "float", "java.lang.Float" -> "REAL";
            case "java.time.Instant", "java.time.LocalDateTime" -> "DATETIME";
            default -> throw new IllegalArgumentException("Unsupported field type: " + field.getType().getName());
        };
    }

    /**
     * Checks whether the field type supports SQL AUTO_INCREMENT.
     *
     * @param field The Java field to check.
     * @return true if the field is an integer or long type.
     */
    public static boolean shouldAutoIncrement(Field field) {
        return switch (field.getType().getName()) {
            case "int", "java.lang.Integer", "long", "java.lang.Long" -> true;
            default -> false;
        };
    }

    /**
     * Sets a field value on an entity, converting String values to Instant or LocalDateTime
     * when the target field type requires it.
     *
     * @param entity The entity instance.
     * @param field The field to set.
     * @param value The value to set.
     * @param <T> The entity type.
     * @throws IllegalAccessException if the field is inaccessible.
     */
    public static <T> void setValueSafely(T entity, Field field, Object value) throws IllegalAccessException {
        if (value != null) {
            Class<?> fieldType = field.getType();
            if (value instanceof String stringValue) {
                if (fieldType == Instant.class) {
                    value = Instant.parse(stringValue);
                } else if (fieldType == LocalDateTime.class) {
                    value = LocalDateTime.parse(stringValue.replace(" ", "T"));
                }
            }
        }
        field.set(entity, value);
    }
}
