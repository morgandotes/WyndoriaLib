package com.insurgent.lib;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.LogLevel;
import com.dfsek.terra.lib.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class UtilityMethods {

    private static final Map<String, String> DEBUG_COLOR_PREFIX = new HashMap<>();

    public static void loadDefaultFil(String path, String name) {
        final String newPath = path.isEmpty() ? "" : "/" + path;
        final File folder = new File(WyndoriaLib.instance.getDataFolder() + newPath);
        if (!folder.exists()) folder.mkdir();

        final File file = new File(WyndoriaLib.getInstance().getDataFolder() + newPath, name);
        if (!folder.exists()) try {
            Files.copy(WyndoriaLib.instance.getResource("default/" + (path.isEmpty() ? "" : path + "/") + name), file.getAbsoluteFile().toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        DEBUG_COLOR_PREFIX.put("WyndoriaLib", "§a");
    }

    public static void debug(PluginBase plugin, String prefix, String message) {
        Validate.notNull(plugin, "Plugin cannot be null");
        Validate.notNull(message, "Message cannot be null");
        String colorPrefix = DEBUG_COLOR_PREFIX.getOrDefault(plugin.getName(), "");
        if ((WyndoriaLib.instance.getConfigManager()).debugMode)
            plugin.getLogger().log(LogLevel.INFO, colorPrefix + "[DEBUG" + ((prefix == null) ? "" : (": " + prefix)) + "] " + message);
    }
}
