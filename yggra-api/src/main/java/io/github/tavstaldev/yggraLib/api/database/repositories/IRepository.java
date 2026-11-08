package io.github.tavstaldev.yggraLib.api.database.repositories;

import io.github.tavstaldev.yggraLib.api.database.QueryCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Generic CRUD contract for a database repository.
 *
 * @param <ID> The type of the entity's identifier.
 * @param <T>  The entity type.
 */
public interface IRepository<ID, T> {

    /**
     * Returns the name of the database table backing this repository.
     *
     * @return The table name.
     */
    String getTableName();

    /**
     * Ensures the required table exists and is up to date.
     */
    void checkSchema();

    /**
     * Inserts a new entity into the database.
     *
     * @param entity The entity to add.
     * @return true if the insert succeeded.
     */
    boolean add(T entity);

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id The entity identifier.
     * @return The entity, or null if not found.
     */
    @Nullable T findById(ID id);

    /**
     * Retrieves entities matching the given criteria.
     *
     * @param cacheKey   Optional cache key; null to bypass caching.
     * @param conditions The query conditions to filter by.
     * @return A list of matching entities, or null if the query failed.
     */
    @Nullable List<T> findByCriteria(@Nullable String cacheKey, QueryCondition... conditions);

    /**
     * Retrieves all entities of this type.
     *
     * @return A list of all entities, or null if the query failed.
     */
    @Nullable List<T> findAll();

    /**
     * Updates an existing entity in the database.
     *
     * @param id     The identifier of the entity to update.
     * @param entity The entity containing the updated data.
     * @return true if the update succeeded.
     */
    boolean update(ID id, T entity);

    /**
     * Deletes an entity from the database.
     *
     * @param entity The entity to delete.
     * @return true if the deletion succeeded.
     */
    boolean delete(T entity);

    /**
     * Deletes an entity by its identifier.
     *
     * @param id The identifier of the entity to delete.
     * @return true if the deletion succeeded.
     */
    boolean deleteById(ID id);

    /**
     * Deletes entities matching the given criteria.
     *
     * @param conditions The query conditions to filter by.
     * @return true if the deletion succeeded.
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean deleteByCriteria(QueryCondition... conditions);

    /**
     * Checks whether an entity with the given identifier exists.
     *
     * @param id The identifier to check.
     * @return true if the entity exists.
     */
    boolean existsById(ID id);

    /**
     * Returns the total number of entities in the table.
     *
     * @return The entity count.
     */
    long count();
}
