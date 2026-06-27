package io.github.tavstaldev.yggra.core.database;

public record QueryCondition(String columnName, Object value) {
    public static QueryCondition eq(String column, Object val) {
        return new QueryCondition(column, val);
    }
}