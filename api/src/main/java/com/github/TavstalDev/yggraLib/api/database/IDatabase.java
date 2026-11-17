package com.github.TavstalDev.yggraLib.api.database;

/**
 * Defines the lifecycle contract for a database backend.
 */
public interface IDatabase {

    /**
     * Initializes the database connection and prepares the backend for use.
     */
    void load();

    /**
     * Closes the database connection and releases resources.
     */
    void unload();

    /**
     * Ensures the database schema is up to date, creating or migrating tables as needed.
     */
    void checkSchema();
}
