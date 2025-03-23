package com.insurgent.lib.database;

import cn.nukkit.Player;
import com.dfsek.terra.lib.commons.lang3.Validate;
import com.insurgent.lib.api.player.RPGPlayerData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SynchronizedDataHolder implements OfflineDataHolder {
    private final RPGPlayerData playerData;

    private boolean sync;

    public SynchronizedDataHolder(RPGPlayerData playerData) {
        this.playerData = playerData;
    }

    public RPGPlayerData getRPGPlayerData() {
        return playerData;
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return playerData.getUniqueId();
    }

    @NotNull
    public Player getPlayer() {
        return playerData.getPlayer();
    }

    /**
     * @return If the synchronized data has already been loaded.
     */
    public boolean isSynchronized() {
        return sync;
    }

    public void markAsSynchronized() {
        Validate.isTrue(!sync, "Data holder already marked synchronized");
        sync = true;
    }

}
