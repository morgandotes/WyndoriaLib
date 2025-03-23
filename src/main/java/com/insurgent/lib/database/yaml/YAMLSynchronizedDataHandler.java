package com.insurgent.lib.database.yaml;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import com.insurgent.lib.database.OfflineDataHolder;
import com.insurgent.lib.database.SynchronizedDataHandler;
import com.insurgent.lib.database.SynchronizedDataHolder;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class YAMLSynchronizedDataHandler<H extends SynchronizedDataHolder, O extends OfflineDataHolder> implements SynchronizedDataHandler<H, O> {
    private final Plugin owning;

    public YAMLSynchronizedDataHandler(Plugin owning) {
        this.owning = Objects.requireNonNull(owning, "Plugin cannot be null");
    }

    @Override
    public void saveData(H playerData, boolean autosave) {
        final Config config = getUserFile(playerData);
        saveInSection(playerData, config.getRootSection());
        config.save();
    }

    public abstract void saveInSection(H playerData, ConfigSection config);

    @Override
    public CompletableFuture<Void> loadData(H playerData) {
        return CompletableFuture.runAsync(() -> {
            try {
                Config config = getUserFile(playerData);
                loadFromSection(playerData, config.getRootSection());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public abstract void loadFromSection(H playerData, ConfigSection config);

    private Config getUserFile(H playerData) {
        File dataFolder = new File(owning.getDataFolder(), "userdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        return new Config(new File(dataFolder, playerData.getUniqueId().toString() + ".yml"), Config.YAML);
    }
}
