package io.github.tavstaldev.yggra.api.database.repositories;

import com.zaxxer.hikari.HikariDataSource;
import io.github.tavstaldev.yggra.api.cache.ICache;
import io.github.tavstaldev.yggra.api.database.DatabaseHelper;
import io.github.tavstaldev.yggra.api.database.QueryCondition;
import io.github.tavstaldev.yggra.api.database.annotations.Column;
import io.github.tavstaldev.yggra.api.database.annotations.Table;
import io.github.tavstaldev.yggra.api.database.enums.EColumnKind;
import io.github.tavstaldev.yggra.api.logger.YggraLogger;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class RepositoryBase<ID, T> implements IRepository<ID, T> {
    protected final ICache cache;
    protected final YggraLogger logger;
    protected final HikariDataSource dataSource;
    protected final String tableName;
    protected final long cacheDuration;
    protected final Field idField;
    protected final String idColumn;
    protected final Class<T> entityClass;
    protected final Map<String, Field> columnToFieldMap = new LinkedHashMap<>();
    protected final Set<String> criteriaCacheKeys = ConcurrentHashMap.newKeySet();
    protected final boolean cacheEnabled;

    protected String createTableStatement;
    protected String addSqlStatement;
    protected String findByIdSqlStatement;
    protected String findAllSqlStatement;
    protected String updateByIdSqlStatement;
    protected String deleteByIdSqlStatement;
    protected String countSqlStatement;
    protected String existsByIdSqlStatement;
    protected final List<String> createColumnDefinitions = new ArrayList<>();

    public RepositoryBase(ICache cache, YggraLogger logger, Class<T> entityClass, HikariDataSource dataSource, long cacheDuration, @Nullable String tablePrefix) {
        this.cache = cache;
        this.logger = logger;
        this.dataSource = dataSource;
        this.cacheDuration = cacheDuration;
        this.entityClass = entityClass;
        this.cacheEnabled = cacheDuration > 0;

        if (!entityClass.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Class missing @Table annotation: " + entityClass.getName());
        }

        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        tableName = (tablePrefix != null ? tablePrefix : "") + tableAnnotation.value();

        Field tempIdField = null;
        String tempIdColumn = null;

        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true); // Allow reading values quickly later

            if (!field.isAnnotationPresent(Column.class))
                continue;

            Column annotation = field.getAnnotation(Column.class);
            String columnName = annotation.value();
            EColumnKind columnKind = annotation.kind();
            int columnLength = annotation.length();
            columnToFieldMap.put(columnName, field);

            String sqlType = resolveSqlType(field, columnLength, columnKind);
            String columnDefinition = columnName + " " + sqlType;
            if (columnKind == EColumnKind.PRIMARY) {
                tempIdField = field;
                tempIdColumn = columnName;
                columnDefinition += " PRIMARY KEY";
                if (DatabaseHelper.shouldAutoIncrement(field))
                    columnDefinition += getAutoIncrementText();
            } else {
                if (columnKind == EColumnKind.UNIQUE)
                    columnDefinition += " UNIQUE";

                if (!annotation.nullable()) {
                    columnDefinition += " NOT NULL";
                }
            }
            createColumnDefinitions.add(columnDefinition);
        }

        this.idField = tempIdField;
        this.idColumn = tempIdColumn;
    }

    protected abstract String resolveSqlType(Field field, int columnLength, EColumnKind columnKind);

    protected abstract String getAutoIncrementText();

    protected abstract boolean getExistsByIdResult(ResultSet resultSet) throws SQLException;

    protected abstract void setStatementParameter(PreparedStatement statement, int pos, Object value) throws SQLException;

    protected abstract boolean performDeleteByCriteria(Connection connection, String whereClause, Object[] args) throws SQLException;

    //#region IRepository implementations
    @Override
    public String getTableName() {
        return tableName;
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
                    setStatementParameter(statement, ordinal, value);
                    ordinal++;
                }

                if (statement.executeUpdate() == 0) {
                    logger.error("Failed to add entity to the database: No rows affected.");
                    return false;
                }

                if (cacheEnabled) {
                    Object id = idField.get(entity);
                    //noinspection unchecked
                    evictAllCaches((ID) id);
                    cache.put("db:" + tableName + ":" + idField.get(entity), entity, cacheDuration);
                }
                return true;
            }
        }
        catch (Exception ex) {
            logger.error("Failed to add entity to the database", ex);
            return false;
        }
    }

    @Override
    public @Nullable T findById(ID id) {
        String cacheKey = "db:" + tableName + ":" + id.toString();
        if (cacheEnabled) {
            T cachedEntity = cache.get(cacheKey, entityClass);
            if (cachedEntity != null) {
                return cachedEntity;
            }
        }

        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                logger.error("Failed to create database connection for findById.");
                return null;
            }

            try (PreparedStatement statement = connection.prepareStatement(findByIdSqlStatement)) {
                setStatementParameter(statement, 1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        T entity = entityClass.getDeclaredConstructor().newInstance();
                        for (Map.Entry<String, Field> entry : columnToFieldMap.entrySet()) {
                            String columnName = entry.getKey();
                            Field field = entry.getValue();
                            Object value = resultSet.getObject(columnName);
                            DatabaseHelper.setValueSafely(entity, field, value);
                        }
                        if (cacheEnabled) {
                            cache.put(cacheKey, entity, cacheDuration);
                        }
                        return entity;
                    }
                }
                return null;
            }
        }
        catch (Exception ex) {
            logger.error("Failed to find entity by ID in the database", ex);
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
        if (cacheEnabled && cacheKey != null) {
            finalCacheKey = "db:" + tableName + ":" + cacheKey;
            Class<T[]> arrayClass = (Class<T[]>) Array.newInstance(entityClass, 0).getClass();
            T[] cachedArray = cache.get(finalCacheKey, arrayClass);
            if (cachedArray != null) {
                return Arrays.asList(cachedArray);
            }
        }

        List<Object> argsList = new ArrayList<>(conditions.length);

        String whereClause = Arrays.stream(conditions)
                .map(cond -> {
                    argsList.add(cond.value);
                    return cond.columnName + " " + cond.operator.getSqlOperator() + " ?";
                })
                .collect(Collectors.joining(" AND "));

        Object[] args = argsList.toArray();

        String dynamicSql = "SELECT * FROM " + tableName +  " WHERE " + whereClause + ";";

        List<T> result = this.query(dynamicSql, args);
        if (cacheEnabled && result != null && !result.isEmpty() && !finalCacheKey.isEmpty()) {
            criteriaCacheKeys.add(finalCacheKey);
            cache.put(finalCacheKey, result.toArray((T[])Array.newInstance(entityClass, result.size())), cacheDuration);
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable List<T> findAll() {
        String cacheKey = "db:" + tableName + ":all";
        if (cacheEnabled) {
            Class<T[]> arrayClass = (Class<T[]>) Array.newInstance(entityClass, 0).getClass();
            T[] cachedArray = cache.get(cacheKey, arrayClass);
            if (cachedArray != null) {
                return Arrays.asList(cachedArray);
            }
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
                if (cacheEnabled) {
                    cache.put(cacheKey, entities.toArray((T[]) Array.newInstance(entityClass, entities.size())), cacheDuration);
                }
                return entities;
            }
        }
        catch (Exception ex) {
            logger.error("Failed to find all entities in the database", ex);
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
                    if (field == idField)
                        continue;

                    Object value = field.get(entity);
                    setStatementParameter(statement, ordinal, value);
                    ordinal++;
                }
                // WHERE clause
                setStatementParameter(statement, ordinal, id);
                if (statement.executeUpdate() > 0) {
                    if (cacheEnabled) {
                        evictAllCaches(id);
                        cache.put("db:" + tableName + ":" + id, entity, cacheDuration);
                    }
                    return true;
                }
                return false;
            }
        }
        catch (Exception ex) {
            logger.error("Failed to update entity from the database", ex);
            return false;
        }
    }

    @Override
    public boolean delete(T entity) {
        try {
            Object id = idField.get(entity);
            //noinspection unchecked
            return deleteById((ID)id);
        }
        catch (Exception ex) {
            logger.error("Failed to access id field.", ex);
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
                setStatementParameter(statement, 1, id);
                if (statement.executeUpdate() > 0) {
                    if (cacheEnabled) {
                        evictAllCaches(id);
                    }
                    return true;
                }
                return false;
            }
        }
        catch (Exception ex) {
            logger.error("Failed to delete entity by id from the database", ex);
            return false;
        }
    }

    @Override
    public boolean deleteByCriteria(QueryCondition... conditions) {
        if (conditions == null || conditions.length == 0)
            return false;

        List<Object> argsList = new ArrayList<>(conditions.length);

        String whereClause = Arrays.stream(conditions)
                .map(cond -> {
                    argsList.add(cond.value);
                    return cond.columnName + " " + cond.operator.getSqlOperator() + " ?";
                })
                .collect(Collectors.joining(" AND "));

        Object[] args = argsList.toArray();


        try (Connection connection = dataSource.getConnection()) {
            if (connection == null) {
                logger.error("Failed to create database connection for existsById.");
                return false;
            }

            return performDeleteByCriteria(connection, whereClause, args);
        }
        catch (Exception ex) {
            logger.error("Failed to delete entity by criteria from the database", ex);
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
                setStatementParameter(statement, 1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return getExistsByIdResult(resultSet);
                    }
                    return false;
                }
            }
        }
        catch (Exception ex) {
            logger.error("Failed to check if entity exists in the database", ex);
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
            logger.error("Failed to count entities in the database", ex);
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
                    setStatementParameter(statement, i + 1, args[i]);
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

    protected boolean deleteByCriteriaWithReturning(Connection connection, String whereClause, Object[] args) throws SQLException {
        String dynamicSql = "DELETE FROM " + tableName + " WHERE " + whereClause + " RETURNING " + idColumn + ";";

        try (PreparedStatement statement = connection.prepareStatement(dynamicSql)) {

            for (int i = 0; i < args.length; i++) {
                setStatementParameter(statement, i + 1, args[i]);
            }

            if (cacheEnabled) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        String id = rs.getString(1);
                        cache.evict("db:" + tableName + ":" + id);
                    }
                }
                evictAllCaches(null);
                return true;
            }
            return statement.executeUpdate() > 0;
        }
    }

    protected boolean deleteByCriteriaWithoutReturning(Connection connection, String whereClause, Object[] args) throws SQLException {

        String selectSql = "SELECT " + idColumn +
                " FROM " + tableName + " WHERE " + whereClause + " FOR UPDATE;";
        String deleteSql = "DELETE FROM " + tableName + " WHERE " + whereClause + ";";

        connection.setAutoCommit(false);
        try {
            List<Object> ids = new ArrayList<>();

            try (PreparedStatement select = connection.prepareStatement(selectSql)) {
                for (int i = 0; i < args.length; i++)
                    setStatementParameter(select, i + 1, args[i]);
                try (ResultSet rs = select.executeQuery()) {
                    while (rs.next())
                        ids.add(rs.getObject(1));
                }
            }

            try (PreparedStatement delete = connection.prepareStatement(deleteSql)) {
                for (int i = 0; i < args.length; i++)
                    setStatementParameter(delete, i + 1, args[i]);
                delete.executeUpdate();
            }

            connection.commit();

            if (cacheEnabled) {
                // Evict collected IDs
                ids.forEach(id -> cache.evict("db:" + tableName + ":" + id));
                evictAllCaches(null);
            }
            return true;

        } catch (Exception ex) {
            connection.rollback();
            logger.error("Failed to delete entities by criteria (no returning) in the database", ex);
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    //#endregion

    protected void evictAllCaches(@Nullable ID id) {
        if (id != null)
            cache.evict("db:" + tableName + ":" + id);
        cache.evict("db:" + tableName + ":all");
        for (String key : criteriaCacheKeys) {
            cache.evict(key);
        }
        criteriaCacheKeys.clear();
    }
}
