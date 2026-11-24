package com.github.TavstalDev.yggraLib.api.database.repositories.impl;

import com.zaxxer.hikari.HikariDataSource;
import com.github.TavstalDev.yggraLib.api.IYggraPlugin;
import com.github.TavstalDev.yggraLib.api.cache.ICache;
import com.github.TavstalDev.yggraLib.api.database.DatabaseHelper;
import com.github.TavstalDev.yggraLib.api.database.annotations.Column;
import com.github.TavstalDev.yggraLib.api.database.enums.EColumnKind;
import com.github.TavstalDev.yggraLib.api.database.repositories.RepositoryBase;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository implementation for MySQL and MariaDB databases.
 *
 * @param <ID> the entity identifier type
 * @param <T>  the entity type
 */
@SuppressWarnings("unused")
public class MySqlRepository<ID, T> extends RepositoryBase<ID, T> {
    /** {@code true} if the connected database is MariaDB. */
    private final boolean isMariaDb;

    /**
     * Creates a new MySQL or MariaDB repository.
     *
     * @param plugin        the plugin instance used to access services and logging
     * @param entityClass   the entity class handled by this repository
     * @param dataSource    the database data source
     * @param isMariaDb     {@code true} if the target database is MariaDB
     * @param cacheDuration the cache duration in seconds
     * @param tablePrefix   an optional table name prefix
     */
    public MySqlRepository(IYggraPlugin plugin, Class<T> entityClass, HikariDataSource dataSource, boolean isMariaDb, long cacheDuration, @Nullable String tablePrefix) {
       super(plugin.getService(ICache.class), plugin.logger().withModule(MySqlRepository.class), entityClass, dataSource, cacheDuration, tablePrefix);
       this.isMariaDb = isMariaDb;

        Set<String> keySet = columnToFieldMap.keySet();
        String columns = String.join(", ", keySet);
        String placeholders = keySet.stream()
                .map(c -> "?")
                .collect(Collectors.joining(", "));
        String updatePlaceholders = keySet.stream()
                .filter(c -> !Objects.equals(c, idColumn))
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
        createColumnDefinitions.clear();
    }

    /** {@inheritDoc} */
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

                        Field field = columnToFieldMap.get(columnName);
                        Column annotation = field.getAnnotation(Column.class);
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
            logger.error("Failed to check schema in MySQL/MariaDB database", ex);
        }
    }

    //#region Abstract implementations

    @Override
    protected void setStatementParameter(PreparedStatement statement, int ordinal, Object value) throws SQLException {
        if (value == null) {
            statement.setNull(ordinal, Types.NULL);
        } else if (value instanceof UUID) {
            statement.setString(ordinal, value.toString());
        } else if (value instanceof Enum) {
            statement.setString(ordinal, ((Enum<?>) value).name());
        } else {
            statement.setObject(ordinal, value);
        }
    }

    @Override
    protected boolean performDeleteByCriteria(Connection connection, String whereClause, Object[] args) throws SQLException {
        if (isMariaDb)
            return deleteByCriteriaWithReturning(connection, whereClause, args);
        else
            return deleteByCriteriaWithoutReturning(connection, whereClause, args);
    }

    @Override
    protected String resolveSqlType(Field field, int columnLength, EColumnKind columnKind) {
        return DatabaseHelper.getSqlTypeForField(field, columnLength);
    }

    @Override
    protected String getAutoIncrementText() {
        return " AUTO_INCREMENT";
    }

    @Override
    protected String selectForDeleteSuffix() {
        return " FOR DELETE";
    }

    @Override
    protected boolean getExistsByIdResult(ResultSet resultSet) throws SQLException {
        return resultSet.getInt(1) == 1;
    }

    //#endregion
}