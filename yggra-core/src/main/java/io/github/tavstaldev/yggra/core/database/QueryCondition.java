package io.github.tavstaldev.yggra.core.database;

import io.github.tavstaldev.yggra.core.database.enums.EQueryOperator;

public record QueryCondition(String columnName, EQueryOperator operator, Object value) {

    public static QueryCondition eq(String column, Object val) {
        return new QueryCondition(column, EQueryOperator.EQUALS, val);
    }

    public static QueryCondition ne(String column, Object val) {
        return new QueryCondition(column, EQueryOperator.NOT_EQUALS, val);
    }

    public static QueryCondition gt(String column, Object val) {
        return new QueryCondition(column, EQueryOperator.GREATER_THAN, val);
    }

    public static QueryCondition gte(String column, Object val) {
        return new QueryCondition(column, EQueryOperator.GREATER_THAN_OR_EQUAL, val);
    }

    public static QueryCondition lt(String column, Object val) {
        return new QueryCondition(column, EQueryOperator.LESS_THAN, val);
    }

    public static QueryCondition lte(String column, Object val) {
        return new QueryCondition(column, EQueryOperator.LESS_THAN_OR_EQUAL, val);
    }

    public static QueryCondition like(String column, String pattern) {
        return new QueryCondition(column, EQueryOperator.LIKE, pattern);
    }
}