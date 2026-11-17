package io.github.tavstaldev.yggraLib.api.models;

import java.util.UUID;

/**
 * Represents a simple entity reference identified by a unique ID.
 * This class is used as a platform-independent entity model.
 */
public class YggraEntity {
    private final UUID _uuid;

    /**
     * Creates a new entity reference.
     *
     * @param uuid the unique ID of the entity
     */
    public YggraEntity(UUID uuid) {
        this._uuid = uuid;
    }

    /**
     * Returns the unique ID of this entity.
     *
     * @return the entity UUID
     */
    public UUID getUniqueId() {
        return _uuid;
    }
}
