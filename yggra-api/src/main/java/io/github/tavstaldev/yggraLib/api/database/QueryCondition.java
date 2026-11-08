package io.github.tavstaldev.yggraLib.api.database;

import io.github.tavstaldev.yggraLib.api.database.enums.EQueryOperator;

public class QueryCondition {
    public String columnName;
    public EQueryOperator operator;
    public Object value;

    public QueryCondition(String columnName, EQueryOperator operator, Object value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Creates a condition where the column equals the given value.
     *
     * @param column The column name.
     * @param val    The value to match.
     * @return A new {@code QueryCondition}.
     */
    public static QueryCondition eq(String column, Object val) {
        return new QueryCondition(column, EQueryOperator.EQUALS, val);
    }

    /**
     * Creates a condition where the column does not equal the given value.
     *
     * @param column The column name.
     * @param val    The value to exclude.
     * @return A new {@code QueryCondition}.
     */
    public static QueryCondition ne(String column, Object val) {
        return new QueryCondition(column, EQueryOperator.NOT_EQUALS, val);
    }

    /**
     * Creates a condition where the column is greater than the given value.
     *
     * @param column The column name.
     * @param val    The threshold value.
     * @return A new {@code QueryCondition}.
     */
    public static QueryCondition gt(String column, Object val) {
        return new QueryCondition(column, EQueryOperator.GREATER_THAN, val);
    }

    /**
     * Creates a condition where the column is greater than or equal to the given value.
     *
     * @param column The column name.
     * @param val    The threshold value.
     * @return A new {@code QueryCondition}.
     */
    public static QueryCondition gte(String column, Object val) {
        return new QueryCondition(column, EQueryOperator.GREATER_THAN_OR_EQUAL, val);
    }

    /**
     * Creates a condition where the column is less than the given value.
     *
     * @param column The column name.
     * @param val    The threshold value.
     * @return A new {@code QueryCondition}.
     */
    public static QueryCondition lt(String column, Object val) {
        return new QueryCondition(column, EQueryOperator.LESS_THAN, val);
    }

    /**
     * Creates a condition where the column is less than or equal to the given value.
     *
     * @param column The column name.
     * @param val    The threshold value.
     * @return A new {@code QueryCondition}.
     */
    public static QueryCondition lte(String column, Object val) {
        return new QueryCondition(column, EQueryOperator.LESS_THAN_OR_EQUAL, val);
    }

    /**
     * Creates a condition where the column matches a LIKE pattern.
     *
     * @param column  The column name.
     * @param pattern The SQL LIKE pattern (e.g. {@code "%foo%"}).
     * @return A new {@code QueryCondition}.
     */
    public static QueryCondition like(String column, String pattern) {
        return new QueryCondition(column, EQueryOperator.LIKE, pattern);
    }
}