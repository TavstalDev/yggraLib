package com.github.TavstalDev.yggraLib.api.database.enums;

/**
 * SQL comparison operators used in query conditions.
 */
public enum EQueryOperator {
    EQUALS("="),
    NOT_EQUALS("<>"),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    LIKE("LIKE"),
    IN("IN");

    private final String sqlOperator;

    EQueryOperator(String sqlOperator) {
        this.sqlOperator = sqlOperator;
    }

    /**
     * Returns the SQL string representation of this operator.
     *
     * @return The SQL operator (e.g. "=", "<>", "LIKE").
     */
    public String getSqlOperator() {
        return sqlOperator;
    }

}
