package io.github.tavstaldev.yggraLib.api.database.repositories;

import com.zaxxer.hikari.HikariDataSource;
import io.github.tavstaldev.yggraLib.api.cache.ICache;
import io.github.tavstaldev.yggraLib.api.database.DatabaseHelper;
import io.github.tavstaldev.yggraLib.api.database.QueryCondition;
import io.github.tavstaldev.yggraLib.api.database.annotations.Column;
import io.github.tavstaldev.yggraLib.api.database.annotations.Table;
import io.github.tavstaldev.yggraLib.api.database.enums.EColumnKind;
import io.github.tavstaldev.yggraLib.api.logger.YggraLogger;
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

/**
 * Base implementation of {@link IRepository} for annotation-based entities.
 * It provides shared CRUD logic, caching, and SQL statement handling.
 *
 * @param <ID> the entity identifier type
 * @param <T>  the entity type
 */
public abstract class RepositoryBase<ID, T> implements IRepository<ID, T> {
    /** The cache used for entity lookups. */
    protected final ICache cache;
    /** The logger used by this repository. */
    protected final YggraLogger logger;
    /** The data source used for database connections. */
    protected final HikariDataSource dataSource;
    /** The name of the database table. */
    protected final String tableName;
    /** The cache duration in seconds. */
    protected final long cacheDuration;
    /** The field that stores the entity identifier. */
    protected final Field idField;
    /** The database column name for the entity identifier. */
    protected final String idColumn;
    /** The entity class handled by this repository. */
    protected final Class<T> entityClass;
    /** A map from column names to entity fields. */
    protected final Map<String, Field> columnToFieldMap = new LinkedHashMap<>();
    /** Cache keys created for criteria-based queries. */
    protected final Set<String> criteriaCacheKeys = ConcurrentHashMap.newKeySet();
    /** {@code true} if caching is enabled for this repository. */
    protected final boolean cacheEnabled;

    /** SQL statement used to create the table. */
    protected String createTableStatement;
    /** SQL statement used to insert an entity. */
    protected String addSqlStatement;
    /** SQL statement used to find an entity by ID. */
    protected String findByIdSqlStatement;
    /** SQL statement used to find all entities. */
    protected String findAllSqlStatement;
    /** SQL statement used to update an entity by ID. */
    protected String updateByIdSqlStatement;
    /** SQL statement used to delete an entity by ID. */
    protected String deleteByIdSqlStatement;
    /** SQL statement used to count entities. */
    protected String countSqlStatement;
    /** SQL statement used to check if an entity exists by ID. */
    protected String existsByIdSqlStatement;
    /** Column definitions used when creating the table. */
    protected final List<String> createColumnDefinitions = new ArrayList<>();

    /**
     * Creates a new repository base for the given entity class.
     *
     * @param cache         the cache to use, or {@code null} if not available
     * @param logger        the logger to use
     * @param entityClass   the entity class handled by this repository
     * @param dataSource    the database data source
     * @param cacheDuration the cache duration in seconds; values below or equal to 0 disable caching
     * @param tablePrefix   an optional table name prefix
     * @throws IllegalArgumentException if the entity class is missing the {@link Table} annotation
     */
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

    /**
     * Resolves the SQL column type for a field.
     *
     * @param field        the entity field
     * @param columnLength the configured column length
     * @param columnKind   the column kind
     * @return the SQL type name
     */
    protected abstract String resolveSqlType(Field field, int columnLength, EColumnKind columnKind);

    /**
     * Returns the SQL fragment used for auto-increment columns.
     *
     * @return the auto-increment SQL fragment, or an empty string if not needed
     */
    protected abstract String getAutoIncrementText();

    /**
     * Reads the result of an "exists by ID" query.
     *
     * @param resultSet the query result set
     * @return {@code true} if the entity exists, otherwise {@code false}
     * @throws SQLException if reading from the result set fails
     */
    protected abstract boolean getExistsByIdResult(ResultSet resultSet) throws SQLException;

    /**
     * Sets a parameter value on a prepared statement.
     *
     * @param statement the prepared statement
     * @param pos       the parameter position
     * @param value     the value to set
     * @throws SQLException if setting the parameter fails
     */
    protected abstract void setStatementParameter(PreparedStatement statement, int pos, Object value) throws SQLException;

    /**
     * Deletes entities that match the given criteria.
     *
     * @param connection  the active database connection
     * @param whereClause the SQL WHERE clause without the {@code WHERE} keyword
     * @param args        the parameter values for the WHERE clause
     * @return {@code true} if the operation completed successfully, otherwise {@code false}
     * @throws SQLException if the delete operation fails
     */
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

    /**
     * Runs a custom SQL query and maps the result to entities.
     *
     * @param sql  the SQL query to execute
     * @param args the query parameter values
     * @return a list of mapped entities; the list may be empty if no rows are found or an error occurs
     */
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

    /**
     * Deletes entities by criteria and uses a {@code RETURNING} clause to collect deleted IDs.
     *
     * @param connection  the active database connection
     * @param whereClause the SQL WHERE clause without the {@code WHERE} keyword
     * @param args        the parameter values for the WHERE clause
     * @return {@code true} if the operation completed successfully, otherwise {@code false}
     * @throws SQLException if the delete operation fails
     */
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

    /**
     * Deletes entities by criteria without using a {@code RETURNING} clause.
     * This method selects matching IDs first so it can clear related cache entries.
     *
     * @param connection  the active database connection
     * @param whereClause the SQL WHERE clause without the {@code WHERE} keyword
     * @param args        the parameter values for the WHERE clause
     * @return {@code true} if the operation completed successfully, otherwise {@code false}
     * @throws SQLException if the delete operation fails
     */
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

    /**
     * Clears cached entries related to this repository.
     *
     * @param id the entity ID to evict, or {@code null} to skip the single-entity cache entry
     */
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
