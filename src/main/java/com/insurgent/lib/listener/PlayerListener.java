package com.insurgent.lib.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import com.insurgent.lib.WyndoriaLib;
import com.insurgent.lib.api.player.RPGPlayerData;

public class PlayerListener implements Listener {
    /**
     * Async pre join events are unreliable for some reason so
     * it seems to be better to initialize player data on the
     * lowest priority possible on sync when the player joins.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void loadData(PlayerJoinEvent event) {
        RPGPlayerData data = RPGPlayerData.setup(event.getPlayer());
        //stats
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void registerOfflinePlayers(PlayerQuitEvent event) {
        RPGPlayerData.get(event.getPlayer().getUniqueId()).updatePlayer(null);
    }

}
