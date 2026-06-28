package io.github.tavstaldev.yggra.core.database;

import com.zaxxer.hikari.HikariDataSource;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

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
        if (field.getType().isEnum()) {
            return String.format("VARCHAR(%s)", columnLength > 0 ? columnLength : 64);
        }

        return switch (field.getType().getName()) {
            case "java.lang.String" -> String.format("VARCHAR(%s)", columnLength);
            case "java.util.UUID" -> "VARCHAR(36)";
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
            case "int", "java.lang.Integer" -> true;
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
                } else if (fieldType == UUID.class) {
                    value = UUID.fromString(stringValue);
                } else if (fieldType.isEnum()) {
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    Object enumValue = Enum.valueOf((Class<Enum>) fieldType, stringValue);
                    value = enumValue;
                }
            }
        }
        field.set(entity, value);
    }

    /**
     * Checks if the given class represents a composite key type.
     * <p>
     * A composite key is any type that is not a primitive, String, UUID, Number, or Boolean.
     * These are considered "simple" types and are not composite keys.
     *
     * @param clazz the class to check
     * @return true if the class is a composite key type, false otherwise
     */
    public static boolean isCompositeKey(Class<?> clazz) {
        return !(clazz.isPrimitive() ||
                clazz == String.class ||
                clazz == UUID.class ||
                Number.class.isAssignableFrom(clazz) ||
                clazz == Boolean.class);
    }

    public static boolean isMariaDB(HikariDataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                return false;
            }
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            return databaseProductName != null && databaseProductName.toLowerCase().contains("mariadb");
        } catch (Exception ex) {
            return false;
        }
    }
}
