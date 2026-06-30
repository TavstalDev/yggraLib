package io.github.tavstaldev.yggra.core.database.repositories;

import com.zaxxer.hikari.HikariDataSource;
import io.github.tavstaldev.yggra.core.YggraPlugin;
import io.github.tavstaldev.yggra.core.cache.ICache;
import io.github.tavstaldev.yggra.core.database.DatabaseHelper;
import io.github.tavstaldev.yggra.core.database.QueryCondition;
import io.github.tavstaldev.yggra.core.database.annotations.Column;
import io.github.tavstaldev.yggra.core.database.annotations.Table;
import io.github.tavstaldev.yggra.core.database.enums.EColumnKind;
import io.github.tavstaldev.yggra.core.logger.YggraLogger;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class SqLiteRepository<ID, T> implements IRepository<ID, T> {
    private final ICache cache;
    private final YggraLogger logger;
    private final HikariDataSource dataSource;
    private final String tableName;
    private final long cacheDuration;
    private final Field idField;
    private final String idColumn;
    private final Class<T> entityClass;
    private final Map<String, Field> columnToFieldMap = new LinkedHashMap<>();

    private final String createTableStatement;
    private final String addSqlStatement;
    private final String findByIdSqlStatement;
    private final String findAllSqlStatement;
    private final String updateByIdSqlStatement;
    private final String deleteByIdSqlStatement;
    private final String countSqlStatement;
    private final String existsByIdSqlStatement;

    public SqLiteRepository(YggraPlugin<?> plugin, Class<T> entityClass, HikariDataSource dataSource, long cacheDuration, @Nullable String tablePrefix) {
        this.cache = plugin.cache();
        this.logger = plugin.logger().withModule(SqLiteRepository.class);
        this.dataSource = dataSource;
        this.cacheDuration = cacheDuration;
        this.entityClass = entityClass;

        if (!entityClass.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Class missing @Table annotation: " + entityClass.getName());
        }

        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        tableName = (tablePrefix != null ? tablePrefix : "") + tableAnnotation.value();

        Field tempIdField = null;
        String tempIdColumn = null;

        List<String> createColumnDefinitions = new ArrayList<>();
        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true); // Allow reading values quickly later

            if (field.isAnnotationPresent(Column.class)) {
                var annotation = field.getAnnotation(Column.class);
                String columnName = annotation.value();
                EColumnKind columnKind = annotation.kind();
                int columnLength = annotation.length();
                columnToFieldMap.put(columnName, field);

                String sqlType = DatabaseHelper.getSqlTypeForField(field, columnLength);
                String columnDefinition = columnName + " " + sqlType;
                if (columnKind == EColumnKind.PRIMARY) {
                    tempIdField = field;
                    tempIdColumn = columnName;
                    columnDefinition += " PRIMARY KEY";
                    if (DatabaseHelper.shouldAutoIncrement(field))
                        columnDefinition += " AUTOINCREMENT";
                }
                else {
                    if (columnKind == EColumnKind.UNIQUE)
                        columnDefinition += " UNIQUE";

                    if (!annotation.nullable())
                        columnDefinition += " NOT NULL";
                }
                createColumnDefinitions.add(columnDefinition);
            }
        }

        this.idField = tempIdField;
        this.idColumn = tempIdColumn;

        var keySet = columnToFieldMap.keySet();
        String columns = String.join(", ", keySet);
        String placeholders = keySet.stream()
                .map(c -> "?")
                .collect(Collectors.joining(", "));
        String updatePlaceholders = keySet.stream()
                .map(c -> c + "=?")
                .collect(Collectors.joining(", "));

        String createPlaceholders = String.join(", ", createColumnDefinitions);
        createTableStatement = String.format("CREATE TABLE IF NOT EXISTS %s (%s);", tableName, createPlaceholders);
        addSqlStatement = String.format("INSERT INTO %s (%s) VALUES (%s);", tableName, columns, placeholders);
        findByIdSqlStatement = String.format("SELECT * FROM %s WHERE %s = ?;", tableName, idColumn);
        findAllSqlStatement = String.format("SELECT * FROM %s;", tableName);
        updateByIdSqlStatement = String.format("UPDATE %s SET %s WHERE %s = ?;", tableName, updatePlaceholders, idColumn);
        deleteByIdSqlStatement = String.format("DELETE FROM %s WHERE %s = ?;", tableName, idColumn);
        countSqlStatement = String.format("SELECT COUNT(*) FROM %s;", tableName);
        existsByIdSqlStatement = String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE %s = ?);", tableName, idColumn);
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public void checkSchema() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                logger.error("Failed to create database connection for checkSchema.");
                return;
            }

            // Create the table if it doesn't exist
            try (PreparedStatement statement = connection.prepareStatement(createTableStatement)) {
                if (statement.executeUpdate() > 0) {
                    logger.debug(String.format("Table '%s' created successfully.", tableName));
                    return;
                }
            }

            // Check column integrity
            DatabaseMetaData meta = connection.getMetaData();
            for (String columnName : columnToFieldMap.keySet()) {

                try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
                    if (!rs.next()) { // Column does not exist, alter table
                        logger.warn(String.format("Column '%s' does not exist in table '%s'. Attempting to add it.", columnName, tableName));

                        var field = columnToFieldMap.get(columnName);
                        var annotation = field.getAnnotation(Column.class);
                        int columnLength = annotation.length();
                        boolean nullable = annotation.nullable();
                        // Note: Primary detection is not supported here
                        boolean unique = annotation.kind() == EColumnKind.UNIQUE;

                        String columnDefinition = columnName + " " + DatabaseHelper.getSqlTypeForField(field, columnLength) +
                                (unique ? " UNIQUE" : "") +
                                (nullable ? "" : " NOT NULL");
                        String alterTableSql = String.format("ALTER TABLE %s ADD COLUMN %s;", tableName, columnDefinition);
                        try (PreparedStatement alterStatement = connection.prepareStatement(alterTableSql)) {
                            if (alterStatement.executeUpdate() > 0) {
                                logger.debug(String.format("Column '%s' added successfully to table '%s'.", columnName, tableName));
                            } else {
                                logger.error(String.format("Failed to add column '%s' to table '%s'.", columnName, tableName));
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            logger.error("Failed to check schema in SQLite database", ex);
        }
    }

    @Override
    public boolean add(T entity) {
        try (Connection connection = dataSource.getConnection()) {

            if (connection == null) {
                logger.error("Failed to create database connection for add.");
                return false;
            }

            try (PreparedStatement statement = connection.prepareStatement(addSqlStatement)) {

                int ordinal = 1;
                for (Field field : columnToFieldMap.values()) {
                    Object value = field.get(entity);
                    statement.setObject(ordinal, value);
                    ordinal++;
                }

                if (statement.executeUpdate() == 0) {
                    logger.error("Failed to add entity to SQLite database: No rows affected.");
                    return false;
                }

                cache.put("db:" + tableName + ":" + idField.get(entity), entity, cacheDuration);
                cache.evict("db:" + tableName + ":all");
                return true;
            }
        }
        catch (Exception ex) {
            logger.error("Failed to add entity to SQLite database", ex);
            return false;
        }
    }

    @Override
    public @Nullable T findById(ID id) {
        String cacheKey = "db:" + tableName + ":" + id;
        T cachedEntity = cache.get(cacheKey, entityClass);
        if (cachedEntity != null) {
            return cachedEntity;
        }

       try (Connection connection = dataSource.getConnection()) {
           if (connection == null) {
               logger.error("Failed to create database connection for findById.");
               return null;
           }

           try (PreparedStatement statement = connection.prepareStatement(findByIdSqlStatement)) {
               statement.setObject(1, id);
               try (ResultSet resultSet = statement.executeQuery()) {
                   if (resultSet.next()) {
                       T entity = entityClass.getDeclaredConstructor().newInstance();
                       for (Map.Entry<String, Field> entry : columnToFieldMap.entrySet()) {
                           String columnName = entry.getKey();
                           Field field = entry.getValue();
                           Object value = resultSet.getObject(columnName);
                           DatabaseHelper.setValueSafely(entity, field, value);
                       }
                         cache.put(cacheKey, entity, cacheDuration);
                       return entity;
                   }
               }
               return null;
           }
       }
       catch (Exception ex) {
           logger.error("Failed to find entity by ID in SQLite database", ex);
           return null;
       }
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable List<T> findByCriteria(@Nullable String cacheKey, QueryCondition... conditions) {
        if (conditions == null || conditions.length == 0) {
            return new ArrayList<>();
        }

        String finalCacheKey = "";
        if (cacheKey != null) {
            finalCacheKey = "db:" + tableName + ":" + cacheKey;
            Class<T[]> arrayClass = (Class<T[]>) Array.newInstance(entityClass, 0).getClass();
            T[] cachedArray = cache.get(finalCacheKey, arrayClass);
            if (cachedArray != null) {
                return Arrays.asList(cachedArray);
            }
        }

        String whereClause = Arrays.stream(conditions)
                .map(cond -> cond.columnName() + " " + cond.operator().getSqlOperator() + " ?")
                .collect(Collectors.joining(" AND "));

        String dynamicSql = "SELECT * FROM " + tableName +  " WHERE " + whereClause + ";";
        Object[] args = Arrays.stream(conditions).map(QueryCondition::value).toArray();

        var result = this.query(dynamicSql, args);
        if (result != null && !result.isEmpty() && !finalCacheKey.isEmpty()) {
            cache.put(finalCacheKey, result.toArray((T[])Array.newInstance(entityClass, result.size())), cacheDuration);
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable List<T> findAll() {
        String cacheKey = "db:" + tableName + ":all";
        Class<T[]> arrayClass = (Class<T[]>) Array.newInstance(entityClass, 0).getClass();
        T[] cachedArray = cache.get(cacheKey, arrayClass);
        if (cachedArray != null) {
            return Arrays.asList(cachedArray);
        }

        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                logger.error("Failed to create database connection for findAll.");
                return null;
            }

            try (PreparedStatement statement = connection.prepareStatement(findAllSqlStatement);
                 ResultSet resultSet = statement.executeQuery()) {
                List<T> entities = new ArrayList<>();
                while (resultSet.next()) {
                    T entity = entityClass.getDeclaredConstructor().newInstance();
                    for (Map.Entry<String, Field> entry : columnToFieldMap.entrySet()) {
                        String columnName = entry.getKey();
                        Field field = entry.getValue();
                        Object value = resultSet.getObject(columnName);
                        DatabaseHelper.setValueSafely(entity, field, value);
                    }
                    entities.add(entity);
                }
                cache.put(cacheKey, entities.toArray((T[])Array.newInstance(entityClass, entities.size())), cacheDuration);
                return entities;
            }
        }
        catch (Exception ex) {
            logger.error("Failed to find all entities in SQLite database", ex);
            return null;
        }
    }

    @Override
    public boolean update(ID id, T entity) {
        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                logger.error("Failed to create database connection for delete.");
                return false;
            }

            try (PreparedStatement statement = connection.prepareStatement(updateByIdSqlStatement)) {
                int ordinal = 1;
                for (Field field : columnToFieldMap.values()) {
                    Object value = field.get(entity);
                    statement.setObject(ordinal, value);
                    ordinal++;
                }
                // WHERE clause
                statement.setObject(ordinal, id);
                if (statement.executeUpdate() > 0) {
                    cache.evict("db:" + tableName + ":" + id);
                    cache.evict("db:" + tableName + ":all");
                    cache.put("db:" + tableName + ":" + id, entity, cacheDuration);
                    return true;
                }
                return false;
            }
        }
        catch (Exception ex) {
            logger.error("Failed to delete entity from SQLite database", ex);
            return false;
        }
    }

    @Override
    public boolean delete(T entity) {
        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                logger.error("Failed to create database connection for delete.");
                return false;
            }

            try (PreparedStatement statement = connection.prepareStatement(deleteByIdSqlStatement)) {
                var id = idField.get(entity);
                statement.setObject(1, id);
                if (statement.executeUpdate() > 0) {
                    cache.evict("db:" + tableName + ":" + id);
                    cache.evict("db:" + tableName + ":all");
                    return true;
                }
                return false;
            }
        }
        catch (Exception ex) {
            logger.error("Failed to delete entity from SQLite database", ex);
            return false;
        }
    }

    @Override
    public boolean deleteById(ID id) {
        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                logger.error("Failed to create database connection for deleteById.");
                return false;
            }

            try (PreparedStatement statement = connection.prepareStatement(deleteByIdSqlStatement)) {
                 statement.setObject(1, id);
                if (statement.executeUpdate() > 0) {
                    cache.evict("db:" + tableName + ":" + id);
                    cache.evict("db:" + tableName + ":all");
                    return true;
                }
                return false;
            }
        }
        catch (Exception ex) {
            logger.error("Failed to delete entity by id from SQLite database", ex);
            return false;
        }
    }

    @Override
    public boolean deleteByCriteria(@Nullable String cacheKey, QueryCondition... conditions) {

        if (conditions == null || conditions.length == 0) {
            return false;
        }

        var result = findByCriteria(cacheKey, conditions);

        String whereClause = Arrays.stream(conditions)
                .map(cond -> cond.columnName() + " " + cond.operator().getSqlOperator() + " ?")
                .collect(Collectors.joining(" AND "));

        String dynamicSql = "DELETE FROM " + tableName + " WHERE " + whereClause + " RETURNING " + idColumn + ";";
        Object[] args = Arrays.stream(conditions).map(QueryCondition::value).toArray();

        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                logger.error("Failed to create database connection for existsById.");
                return false;
            }

            try (PreparedStatement statement = connection.prepareStatement(dynamicSql)) {

                for (int i = 0; i < args.length; i++) {
                    statement.setObject(i + 1, args[i]);
                }

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        String id = rs.getString(1);
                        cache.evict("db:" + tableName + ":" + id);
                    }
                }

                if (cacheKey != null)
                    cache.evict("db:" + tableName + ":" + cacheKey);
                cache.evict("db:" + tableName + ":all");
                return true;
            }
        }
        catch (Exception ex) {
            logger.error("Failed to check if entity exists in MySQL/MariaDB database", ex);
            return false;
        }
    }

    @Override
    public boolean existsById(ID id) {
        String cacheKey = "db:" + tableName + ":" + id;
        T cachedEntity = cache.get(cacheKey, entityClass);
        if (cachedEntity != null) {
            return true;
        }

        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                logger.error("Failed to create database connection for existsById.");
                return false;
            }

            try (PreparedStatement statement = connection.prepareStatement(existsByIdSqlStatement)) {

                statement.setObject(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) == 1;
                    }
                    return false;
                }
            }
        }
        catch (Exception ex) {
            logger.error("Failed to check if entity exists in SQLite database", ex);
            return false;
        }
    }

    @Override
    public long count() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                logger.error("Failed to create database connection for count.");
                return -1;
            }

            try (PreparedStatement statement = connection.prepareStatement(countSqlStatement);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
                return 0;
            }
        }
        catch (Exception ex) {
            logger.error("Failed to count entities in SQLite database", ex);
            return -1;
        }
    }
    
    public List<T> query(String sql, Object... args) {
        List<T> results = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                logger.error("Failed to create database connection for query.");
                return results;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                for (int i = 0; i < args.length; i++) {
                    statement.setObject(i + 1, args[i]);
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        T entity = entityClass.getDeclaredConstructor().newInstance();

                        for (Map.Entry<String, Field> entry : columnToFieldMap.entrySet()) {
                            String columnName = entry.getKey();
                            Field field = entry.getValue();

                            Object value = resultSet.getObject(columnName);
                            if (value != null) {
                                DatabaseHelper.setValueSafely(entity, field, value);
                            }
                        }
                        results.add(entity);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Failed to execute custom query: " + sql, ex);
        }
        return results;
    }
}
