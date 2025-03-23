package com.insurgent.lib.api.player;

import cn.nukkit.OfflinePlayer;
import cn.nukkit.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class RPGPlayerData {
    private final UUID playerId;

    @Nullable
    private Player player;

    /**
     * Last time the player either logged in or logged out.
     */
    private long lastLogActivity;

    /**
     * Map used by other plugins to save any type of data. This
     * is typically used by MMOCore and MMOItems to store the player
     * resources when the player logs off.
     */
    private final Map<String, Object> externalData = new HashMap<>();

    public RPGPlayerData(@NotNull Player player) {
        this.playerId = player.getUniqueId();
        this.player = player;
    }

    public RPGPlayerData(@NotNull UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getUniqueId() {
        return playerId;
    }

    @Deprecated
    public long getLastLogin() {
        return lastLogActivity;
    }

    public long getLastLogActivity() {
        return this.lastLogActivity;
    }

    private static final long CACHE_TIME_OUT = 86400000L;

    public boolean isOnline() {
        return (this.player != null);
    }

    public boolean isTimedOut() {
        return (!isOnline() && this.lastLogActivity + CACHE_TIME_OUT < System.currentTimeMillis());
    }

    public @NotNull Player getPlayer() {
        return Objects.<Player>requireNonNull(this.player, "Player is offline");
    }

    public void updatePlayer(@Nullable Player player) {
        this.player = player;
        this.lastLogActivity = System.currentTimeMillis();
    }

    @Nullable
    public <T> T getExternalData(String key, Class<T> objectType) {
        Object found = this.externalData.get(key);
        return (found == null) ? null : (T)found;
    }

    public void setExternalData(String key, Object obj) {
        this.externalData.put(key, obj);
    }

    public boolean hasExternalData(String key) {
        return this.externalData.containsKey(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RPGPlayerData)) return false;

        RPGPlayerData that = (RPGPlayerData) o;
        return getUniqueId().equals(that.getUniqueId());
    }

    @Override
    public int hashCode() {
        return getUniqueId().hashCode();
    }

    private static final Map<UUID, RPGPlayerData> PLAYER_DATA = new WeakHashMap<>();

    public static RPGPlayerData setup(Player player) {
        RPGPlayerData found = PLAYER_DATA.get(player.getUniqueId());
        if (found == null) {
            RPGPlayerData playerData = new RPGPlayerData(player);
            PLAYER_DATA.put(player.getUniqueId(), playerData);
            return playerData;
        }
        found.updatePlayer(player);
        return found;
    }


    @Deprecated
    public static boolean isLoaded(UUID uuid) {
        return has(uuid);
    }

    @NotNull
    public static RPGPlayerData get(@NotNull OfflinePlayer player) {
        return get(player.getUniqueId());
    }

    @NotNull
    public static RPGPlayerData get(@NotNull UUID uuid) {
        return Objects.requireNonNull(PLAYER_DATA.get(uuid), "Player data not loaded");
    }

    @Nullable
    public static RPGPlayerData getOrNull(@NotNull Player player) {
        return getOrNull(player.getUniqueId());
    }

    @Nullable
    public static RPGPlayerData getOrNull(@NotNull UUID uuid) {
        return PLAYER_DATA.get(uuid);
    }

    public static boolean has(@NotNull OfflinePlayer player) {
        return has(player.getUniqueId());
    }

    public static boolean has(@NotNull UUID uuid) {
        return PLAYER_DATA.containsKey(uuid);
    }

    public static Collection<RPGPlayerData> getLoaded() {
        return PLAYER_DATA.values();
    }

    public static void forEachOnline(Consumer<RPGPlayerData> action) {
        for (RPGPlayerData registered : PLAYER_DATA.values()) {
            if (registered.isOnline())
                action.accept(registered);
        }
    }

    public static void flushOfflinePlayerData() {
        Iterator<RPGPlayerData> iterator = PLAYER_DATA.values().iterator();
        while (iterator.hasNext()) {
            RPGPlayerData tempData = iterator.next();
            if (tempData.isTimedOut())
                iterator.remove();
        }
    }
}
