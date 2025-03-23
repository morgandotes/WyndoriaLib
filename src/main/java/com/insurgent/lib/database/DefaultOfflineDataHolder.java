package com.insurgent.lib.database;

import java.util.UUID;

public class DefaultOfflineDataHolder implements OfflineDataHolder{
    private final UUID uuid;

    public DefaultOfflineDataHolder(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUniqueId() {
        return null;
    }
}
