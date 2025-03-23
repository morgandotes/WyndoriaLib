package com.insurgent.lib.database.sql;

import com.insurgent.lib.database.OfflineDataHolder;
import com.insurgent.lib.database.SynchronizedDataHandler;
import com.insurgent.lib.database.SynchronizedDataHolder;

import java.util.concurrent.CompletableFuture;

public abstract class SQLSynchronizedDataHandler<H extends SynchronizedDataHolder, O extends OfflineDataHolder, S extends SQLDataSynchronizer> implements SynchronizedDataHandler<H, O> {
    private final SQLDataSource dataSource;

    public SQLSynchronizedDataHandler(SQLDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SQLDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public CompletableFuture<Void> loadData(H playerdata) {
        return CompletableFuture.runAsync(() -> {
            try {
                newDataSynchronizer(playerdata).synchronize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void close() {
        getDataSource().close();
    }

    public abstract SQLDataSynchronizer newDataSynchronizer(H playerData);
}
