package com.github.TavstalDev.yggraLib.api.database.enums;

import lombok.Getter;

/**
 * SQL comparison operators used in query conditions.
 */
@Getter
public enum EQueryOperator {
    /** Equality comparison ({@code =}). */
    EQUALS("="),
    /** Inequality comparison ({@code <>}). */
    NOT_EQUALS("<>"),
    /** Greater-than comparison ({@code >}). */
    GREATER_THAN(">"),
    /** Greater-than-or-equal comparison ({@code >=}). */
    GREATER_THAN_OR_EQUAL(">="),
    /** Less-than comparison ({@code <}). */
    LESS_THAN("<"),
    /** Less-than-or-equal comparison ({@code <=}). */
    LESS_THAN_OR_EQUAL("<="),
    /** SQL {@code LIKE} pattern matching. */
    LIKE("LIKE"),
    /** SQL {@code IN} membership checking. */
    IN("IN");

    /**
     * -- GETTER --
     *  Returns the SQL string representation of this operator.
     *
     * @return The SQL operator (e.g. {@code =}, {@code <>}, {@code LIKE}).
     */
    private final String sqlOperator;

    EQueryOperator(String sqlOperator) {
        this.sqlOperator = sqlOperator;
    }

}
