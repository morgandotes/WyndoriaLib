package com.insurgent.lib.manager;

import cn.nukkit.utils.Config;
import com.insurgent.lib.WyndoriaLib;

public class ConfigManager {

    public boolean debugMode;
    public int maxSyncTries;

    public void reload() {

        if (WyndoriaLib.instance == null) {
            throw new IllegalStateException("WyndoriaLib instance is not initialized.");
        }

        Config config = WyndoriaLib.instance.getConfig();
        this.debugMode = config.getBoolean("debug");
        this.maxSyncTries = config.getInt("max-sync-tries");
    }
}
