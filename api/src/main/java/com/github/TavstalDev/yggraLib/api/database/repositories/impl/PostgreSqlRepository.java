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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository implementation for PostgreSQL databases.
 *
 * @param <ID> the entity identifier type
 * @param <T>  the entity type
 */
@SuppressWarnings("unused")
public class PostgreSqlRepository<ID, T> extends RepositoryBase<ID, T> {

    /**
     * Creates a new PostgreSQL repository.
     *
     * @param plugin        the plugin instance used to access services and logging
     * @param entityClass   the entity class handled by this repository
     * @param dataSource    the database data source
     * @param cacheDuration the cache duration in seconds
     * @param tablePrefix   an optional table name prefix
     */
    public PostgreSqlRepository(IYggraPlugin plugin, Class<T> entityClass, HikariDataSource dataSource, long cacheDuration, @Nullable String tablePrefix) {
        super(plugin.getService(ICache.class), plugin.logger().withModule(PostgreSqlRepository.class), entityClass, dataSource, cacheDuration, tablePrefix);

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
        existsByIdSqlStatement = String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE %s = ?) AS %s;", tableName, idColumn, "value");
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

            try (PreparedStatement statement = connection.prepareStatement(createTableStatement)) {
                statement.executeUpdate();
                logger.debug(String.format("Table '%s' schema verification complete.", tableName));
            }

            DatabaseMetaData meta = connection.getMetaData();
            for (String columnName : columnToFieldMap.keySet()) {
                try (ResultSet rs = meta.getColumns(null, null, tableName.toLowerCase(), columnName.toLowerCase())) {
                    if (!rs.next()) {
                        logger.warn(String.format("Column '%s' does not exist in table '%s'. Attempting to alter table.", columnName, tableName));

                        Field field = columnToFieldMap.get(columnName);
                        Column annotation = field.getAnnotation(Column.class);
                        int columnLength = annotation.length();
                        boolean nullable = annotation.nullable();
                        // Note: Primary detection is not supported here
                        boolean unique = annotation.kind() == EColumnKind.UNIQUE;

                        String columnDefinition = columnName + " " + DatabaseHelper.getPostgresSqlTypeForField(field, columnLength) +
                                (unique ? " UNIQUE" : "") +
                                (nullable ? "" : " NOT NULL");
                        String alterTableSql = String.format("ALTER TABLE %s ADD COLUMN %s;", tableName, columnDefinition);
                        try (PreparedStatement alterStatement = connection.prepareStatement(alterTableSql)) {
                            alterStatement.executeUpdate();
                            logger.debug(String.format("Column '%s' added successfully to table '%s'.", columnName, tableName));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Failed to check schema in PostgreSql database", ex);
        }
    }

    //#region Abstract implementations

    @Override
    protected void setStatementParameter(PreparedStatement statement, int ordinal, Object value) throws SQLException {
        if (value == null) {
            statement.setNull(ordinal, Types.NULL);
        } else if (value instanceof UUID) {
            statement.setObject(ordinal, value, Types.OTHER);
        } else if (value instanceof Enum) {
            statement.setString(ordinal, ((Enum<?>) value).name());
        } else if (value instanceof LocalDateTime) {
            statement.setTimestamp(ordinal, Timestamp.valueOf((LocalDateTime) value));
        } else if (value instanceof Instant) {
            statement.setTimestamp(ordinal, Timestamp.from((Instant) value));
        } else if (value instanceof LocalDate) {
            statement.setDate(ordinal, Date.valueOf((LocalDate) value));
        } else {
            statement.setObject(ordinal, value);
        }
    }

    @Override
    protected boolean performDeleteByCriteria(Connection connection, String whereClause, Object[] args) throws SQLException {
        return deleteByCriteriaWithReturning(connection, whereClause, args);
    }

    @Override
    protected String resolveSqlType(Field field, int columnLength, EColumnKind columnKind) {
        String sqlType = DatabaseHelper.getPostgresSqlTypeForField(field, columnLength);
        if (columnKind == EColumnKind.PRIMARY && DatabaseHelper.shouldAutoIncrement(field)) {
            sqlType = sqlType.equalsIgnoreCase("BIGINT") ? "BIGSERIAL" : "SERIAL";
        }
        return sqlType;
    }

    @Override
    protected String getAutoIncrementText() {
        return "";
    }

    @Override
    protected String selectForDeleteSuffix() {
        return " FOR DELETE";
    }

    @Override
    protected boolean getExistsByIdResult(ResultSet resultSet) throws SQLException {
        return resultSet.getBoolean("value");
    }

    //#endregion
}
