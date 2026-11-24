package com.github.TavstalDev.yggraLib.api.database.enums;

/**
 * Defines the constraint kind for a database column.
 */
public enum EColumnKind {
    /** A column with no special constraint. */
    DEFAULT,
    /** A column that is part of the primary key. */
    PRIMARY,
    /** A column whose values must be unique. */
    UNIQUE
}
