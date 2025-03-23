package com.insurgent.lib.database;

import com.insurgent.lib.player.modifier.Closeable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SynchronizedDataHandler<H extends SynchronizedDataHolder, O extends OfflineDataHolder> extends Closeable {

    public void setup();

    public void saveData(H playerData, boolean autosave);

    public CompletableFuture<Void> loadData(H playerData);

    public O getOffline(UUID uuid);
}
