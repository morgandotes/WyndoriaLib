package com.insurgent.lib.api.event;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import com.insurgent.lib.database.SynchronizedDataHolder;
import com.insurgent.lib.database.SynchronizedDataManager;

public class AsyncSynchronizedDataLoadEvent extends Event {
    private final SynchronizedDataManager<?, ?> manager;
    private final SynchronizedDataHolder holder;

    private static final HandlerList HANDLERS = new HandlerList();

    public AsyncSynchronizedDataLoadEvent(SynchronizedDataManager<?, ?> manager, SynchronizedDataHolder holder) {
        this.manager = manager;
        this.holder = holder;
    }

    public SynchronizedDataManager<?, ?> getManager() {
        return manager;
    }

    public SynchronizedDataHolder getHolder() {
        return holder;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
