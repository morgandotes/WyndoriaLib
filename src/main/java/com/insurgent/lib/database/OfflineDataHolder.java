package com.insurgent.lib.database;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface OfflineDataHolder {

    @NotNull
    public UUID getUniqueId();
}
