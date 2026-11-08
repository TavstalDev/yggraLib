package io.github.tavstaldev.yggraLib.api.models;

import java.util.UUID;

public class YggraEntity {
    private final UUID _uuid;

    public YggraEntity(UUID _uuid) {
        this._uuid = _uuid;
    }

    public UUID getUniqueId() {
        return _uuid;
    }
}
