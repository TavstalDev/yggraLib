package io.github.tavstaldev.yggra.core.database.repositories;

import com.zaxxer.hikari.HikariDataSource;
import io.github.tavstaldev.yggra.core.YggraPlugin;
import io.github.tavstaldev.yggra.core.database.DatabaseHelper;
import io.github.tavstaldev.yggra.core.database.annotations.Column;
import io.github.tavstaldev.yggra.core.database.enums.EColumnKind;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class PostgreSqlRepository<ID, T> extends RepositoryBase<ID, T> {

    public PostgreSqlRepository(YggraPlugin<?> plugin, Class<T> entityClass, HikariDataSource dataSource, long cacheDuration, @Nullable String tablePrefix) {
        super(plugin.cache(), plugin.logger().withModule(PostgreSqlRepository.class), entityClass, dataSource, cacheDuration, tablePrefix);

        var keySet = columnToFieldMap.keySet();
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
        if (value instanceof UUID uuid) {
            statement.setObject(ordinal, uuid, java.sql.Types.OTHER);
        } else if (value != null && value.getClass().isEnum()) {
            statement.setString(ordinal, ((Enum<?>) value).name());
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
        String sqlType = DatabaseHelper.getSqlTypeForField(field, columnLength);
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
    protected boolean getExistsByIdResult(ResultSet resultSet) throws SQLException {
        return resultSet.getBoolean("value");
    }

    //#endregion
}
