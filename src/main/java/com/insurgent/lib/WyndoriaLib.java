package com.insurgent.lib;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import com.insurgent.lib.api.player.RPGPlayerData;
import com.insurgent.lib.manager.ConfigManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class WyndoriaLib extends PluginBase {
    public static WyndoriaLib instance;
    private final ConfigManager configManager = new ConfigManager();

    @Override
    public void onLoad() {
        getLogger().info("Enabling WyndoriaLib...");
        instance = this;
        getLogger().info("Instance initialized.");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // load player data of online player
        Server.getInstance().getOnlinePlayers().values().forEach(RPGPlayerData::setup);
        this.configManager.reload();
    }

    @Override
    public void onDisable() {

    }

    public void reload() {
        reloadConfig();
        this.configManager.reload();
    }

    public static WyndoriaLib getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

}