package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;

import java.sql.*;

public class DatabaseManager {

    private final AdvancedPaintBattle plugin;
    private Connection connection;
    private boolean enabled;

    public DatabaseManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getConfig().getBoolean("mysql.enabled", false);
        if (enabled) connect();
    }

    private void connect() {
        String host = plugin.getConfig().getString("mysql.host", "localhost");
        int port = plugin.getConfig().getInt("mysql.port", 3306);
        String db = plugin.getConfig().getString("mysql.database", "advancedpaintbattle");
        String user = plugin.getConfig().getString("mysql.username", "root");
        String pass = plugin.getConfig().getString("mysql.password", "");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&autoReconnect=true", user, pass);
            createTables();
            plugin.getLogger().info("[APB] MySQL conectado correctamente.");
        } catch (Exception e) {
            plugin.getLogger().warning("[APB] Error conectando MySQL: " + e.getMessage());
            enabled = false;
        }
    }

    private void createTables() throws SQLException {
        String prefix = plugin.getConfig().getString("mysql.table-prefix", "apb_");
        Statement stmt = connection.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS " + prefix + "stats (" +
                "uuid VARCHAR(36) PRIMARY KEY, name VARCHAR(16), wins INT DEFAULT 0, " +
                "games INT DEFAULT 0, points INT DEFAULT 0, coins INT DEFAULT 0)");
        stmt.close();
    }

    public boolean isEnabled() { return enabled; }
    public Connection getConnection() { return connection; }

    public void close() {
        if (connection != null) try { connection.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
