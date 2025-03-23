package com.insurgent.lib.database.sql;

import cn.nukkit.utils.LogLevel;
import com.insurgent.lib.UtilityMethods;
import com.insurgent.lib.WyndoriaLib;
import com.insurgent.lib.database.SynchronizedDataHolder;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SQLDataSynchronizer<H extends SynchronizedDataHolder> {
    private final SQLDataSource dataSource;
    private final H data;
    private final String tableName, uuidFieldName;
    private final long start = System.currentTimeMillis();

    private int tries;

    public SQLDataSynchronizer(String tableName, String uuidFieldName, SQLDataSource dataSource, H data) {
        this.tableName = tableName;
        this.uuidFieldName = uuidFieldName;
        this.data = data;
        this.dataSource = dataSource;
    }

    public H getData() {
        return data;
    }

    private static final int PERIOD = 10000;

    public void synchronize() {
        // Cancel is player is offline
        if (!data.getRPGPlayerData().isOnline()) {
            UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Stopped data retrieval for `" + data.getUniqueId() + "` as they went offline");
            return;
        }

        tries++;

        // Fields that must be closed afterwards
        @Nullable Connection connection = null;
        @Nullable PreparedStatement prepare = null;
        @Nullable ResultSet result = null;
        boolean retry = false;

        try {
            connection = dataSource.getConnection();
            prepare = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE `" + uuidFieldName + "` = ?;");
            prepare.setString(1, data.getUniqueId().toString());

            UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Trying to load data of " + data.getUniqueId());
            result = prepare.executeQuery();

            // Load data if found
            if (result.next()) {
                if (tries > WyndoriaLib.instance.getConfigManager().maxSyncTries || result.getInt("is_saved") == 1) {
                    confirmReception(connection);
                    loadData(result);
                    if (tries > WyndoriaLib.instance.getConfigManager().maxSyncTries)
                        UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Maximum number of tries reached.");
                    UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Found and loaded data of '" + data.getUniqueId() + "'");
                    UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Time taken: " + (System.currentTimeMillis() - start) + "ms");
                } else {
                    UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Did not load data of '" + data.getUniqueId() + "' as 'is_saved' is set to 0, trying again in " + PERIOD + "ms");
                    retry = true;
                    Thread.sleep(PERIOD);
                }
            } else {
                // Empty player data
                confirmReception(connection);
                loadEmptyData();
                UtilityMethods.debug(dataSource.getPlugin(), "SQL", "Found empty data for '" + data.getUniqueId() + "', loading default...");
            }
        } catch (Exception throwable) {
            dataSource.getPlugin().getLogger().log(LogLevel.WARNING, "Could not load player data of '" + data.getUniqueId() + "':");
            throwable.printStackTrace();
        } finally {
            // Close resources
            try {
                if (result != null) result.close();
                if (prepare != null) prepare.close();
                if (connection != null) connection.close();
            } catch (SQLException exception) {
                dataSource.getPlugin().getLogger().log(LogLevel.WARNING, "Could not load player data of '" + data.getUniqueId() + "':");
                exception.printStackTrace();
            }
        }

        // Synchronize after closing resources
        if (retry) synchronize();

    }

    /**
     * This confirms the loading of player and switches "is_saved" back to 0
     *
     * @param connection Current SQL connection
     * @throws SQLException Any exception. When thrown, the data will not be loaded.
     */
    private void confirmReception(Connection connection) throws SQLException {

        // Confirm reception of inventory
        final PreparedStatement prepared1 = connection.prepareStatement("INSERT INTO " + tableName + "(`uuid`, `is_saved`) VALUES(?, 0) ON DUPLICATE KEY UPDATE `is_saved` = 0;");
        prepared1.setString(1, data.getUniqueId().toString());
        try {
            prepared1.executeUpdate();
        } catch (Exception exception) {
            dataSource.getPlugin().getLogger().log(LogLevel.WARNING, "Could not confirm data sync of " + data.getUniqueId());
            exception.printStackTrace();
        } finally {
            prepared1.close();
        }
    }

    /**
     * Called when the right result set has finally been found.
     *
     * @param result Row found in the database
     */
    public abstract void loadData(ResultSet result) throws SQLException, IOException, ClassNotFoundException;

    /**
     * Called when no data was found.
     */
    public abstract void loadEmptyData();

}