package io.github.tavstaldev.yggraLib.api.database;

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

        switch (field.getType().getName())
        {
            case "java.lang.String": {
                return String.format("VARCHAR(%s)", columnLength);
            }
            case "java.util.UUID": {
                return "VARCHAR(36)";
            }
            case "int":
            case "java.lang.Integer": {
                return "INTEGER";
            }
            case "long":
            case "java.lang.Long":
            {
                return "BIGINT";
            }
            case "boolean":
            case "java.lang.Boolean":
            {
                return "BOOLEAN";
            }
            case "double":
            case "java.lang.Double":
            case "float":
            case "java.lang.Float":
            {
                return "REAL";
            }
            case "java.time.Instant":
            case "java.time.LocalDateTime":
            {
                return "DATETIME";
            }
            default: {
                throw new IllegalArgumentException("Unsupported field type: " + field.getType().getName());
            }
        }
    }

    /**
     * Maps a Java field type to its corresponding PostgreSQL column type.
     *
     * @param field The Java field to map.
     * @param columnLength The column length used for VARCHAR types.
     * @return The PostgreSQL SQL type string.
     * @throws IllegalArgumentException if the field type is not supported.
     */
    public static String getPostgresSqlTypeForField(Field field, int columnLength) {
        if (field.getType().isEnum()) {
            return String.format("VARCHAR(%s)", columnLength > 0 ? columnLength : 64);
        }

        switch (field.getType().getName()) {
            case "java.lang.String": {
                return String.format("VARCHAR(%s)", columnLength);
            }
            case "java.util.UUID": {
                return "UUID";
            }
            case "int":
            case "java.lang.Integer": {
                return "INTEGER";
            }
            case "long":
            case "java.lang.Long":
            {
                return "BIGINT";
            }
            case "boolean":
            case "java.lang.Boolean":
            {
                return "BOOLEAN";
            }
            case "double":
            case "java.lang.Double":
            {
                return "DOUBLE PRECISION";
            }
            case "float":
            case "java.lang.Float":
            {
                return "REAL";
            }
            case "java.time.Instant":
            {
                return "TIMESTAMPTZ";
            }
            case "java.time.LocalDateTime":
            {
                return "TIMESTAMP";
            }
            default: {
                throw new IllegalArgumentException("Unsupported field type: " + field.getType().getName());
            }
        }
    }

    /**
     * Checks whether the field type supports SQL AUTO_INCREMENT.
     *
     * @param field The Java field to check.
     * @return true if the field is an integer or long type.
     */
    public static boolean shouldAutoIncrement(Field field) {
        switch (field.getType().getName()) {
            case "int":
            case "java.lang.Integer": {
                return true;
            }
            default: {
                return false;
            }
        }
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
            if (value instanceof String) {
                String stringValue = (String)value;
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
            else if (value instanceof java.sql.Timestamp) {
                java.sql.Timestamp sqlTimestamp = (java.sql.Timestamp)value;
                if (fieldType == LocalDateTime.class) {
                    value = sqlTimestamp.toLocalDateTime();
                } else if (fieldType == Instant.class) {
                    value = sqlTimestamp.toInstant();
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

    /**
     * Checks whether the given data source connects to a MariaDB database.
     *
     * @param dataSource The HikariCP data source to test.
     * @return true if the underlying database is MariaDB, false otherwise.
     */
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
