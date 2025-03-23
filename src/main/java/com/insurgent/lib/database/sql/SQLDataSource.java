package com.insurgent.lib.database.sql;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.LogLevel;
import com.insurgent.lib.WyndoriaLib;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SQLDataSource {
    private final PluginBase plugin;
    private final HikariDataSource dataSource;

    public SQLDataSource(PluginBase plugin) {
        this.plugin = plugin;

        // Prepare Hikari Config
        final ConfigSection config = plugin.getConfig().getSection("mysql");
        final HikariConfig hikari = new HikariConfig();
        hikari.setPoolName("MMO-hikari");
        hikari.setJdbcUrl("jdbc:mysql://" + config.getString("host", "localhost") + ":" + config.getString("port", "3306") + "/" + config.getString("database", "minecraft"));
        hikari.setUsername(config.getString("user", "root"));
        hikari.setPassword(config.getString("pass", ""));
        hikari.setMaximumPoolSize(config.getInt("maxPoolSize", 10));
        hikari.setMaxLifetime(config.getLong("maxLifeTime", 300000));
        hikari.setConnectionTimeout(config.getLong("connectionTimeOut", 10000));
        hikari.setLeakDetectionThreshold(config.getLong("leakDetectionThreshold", 150000));
        if (config.exists("properties")) {
            ConfigSection properties = config.getSection("properties");
            for (String s : properties.getKeys(false)) {
                hikari.addDataSourceProperty(s, config.getString("properties." + s));
            }
        }

        dataSource = new HikariDataSource(hikari);
    }

    public PluginBase getPlugin() {
        return plugin;
    }

    public void getResult(String sql, Consumer<ResultSet> supplier) {
        execute(connection -> {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                supplier.accept(statement.executeQuery());
            } catch (SQLException exception) {
                WyndoriaLib.instance.getLogger().log(LogLevel.WARNING, "Could not open SQL result statement:");
                exception.printStackTrace();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public CompletableFuture<Void> getResultAsync(String sql, Consumer<ResultSet> supplier) {
        return CompletableFuture.runAsync(() -> getResult(sql, supplier));
    }

    public void executeUpdate(String sql) {
        execute(connection -> {
            try {
                final PreparedStatement statement = connection.prepareStatement(sql);
                try {
                    statement.executeUpdate();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                statement.close();
            } catch (SQLException exception) {
                WyndoriaLib.instance.getLogger().log(LogLevel.WARNING, "Could not open SQL statement:");
                exception.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> executeUpdateAsync(String sql) {
        return CompletableFuture.runAsync(() -> executeUpdate(sql));
    }

    public void execute(Consumer<Connection> execute) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            execute.accept(connection);
        } catch (SQLException e) {
            WyndoriaLib.instance.getLogger().log(LogLevel.WARNING, "Could not open SQL connection: ");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public CompletableFuture<Void> executeAsync(Consumer<Connection> execute) {
        return CompletableFuture.runAsync(() -> execute(execute));
    }

    public Connection getConnection() throws Exception {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null)
            dataSource.close();
    }
}
