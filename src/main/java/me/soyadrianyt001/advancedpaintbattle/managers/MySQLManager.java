package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.*;

public class MySQLManager {

    private final AdvancedPaintBattle plugin;
    private Connection connection;
    private boolean enabled;
    private String prefix;

    public MySQLManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getConfig().getBoolean("mysql.enabled", false);
        this.prefix = plugin.getConfig().getString("mysql.table-prefix", "apb_");
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
                    "jdbc:mysql://" + host + ":" + port + "/" + db +
                    "?useSSL=false&autoReconnect=true&characterEncoding=utf8", user, pass);
            createTables();
            startKeepAlive();
            plugin.getLogger().info("[APB] MySQL conectado correctamente.");
        } catch (Exception e) {
            plugin.getLogger().severe("[APB] Error conectando MySQL: " + e.getMessage());
            enabled = false;
        }
    }

    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        // Tabla de stats
        stmt.execute("CREATE TABLE IF NOT EXISTS " + prefix + "stats (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "name VARCHAR(16) NOT NULL, " +
                "wins INT DEFAULT 0, " +
                "games INT DEFAULT 0, " +
                "points INT DEFAULT 0, " +
                "coins INT DEFAULT 0, " +
                "rank_name VARCHAR(32) DEFAULT 'Aprendiz', " +
                "first_join BIGINT DEFAULT 0, " +
                "last_seen BIGINT DEFAULT 0" +
                ")");

        // Tabla de logros
        stmt.execute("CREATE TABLE IF NOT EXISTS " + prefix + "achievements (" +
                "uuid VARCHAR(36) NOT NULL, " +
                "achievement VARCHAR(64) NOT NULL, " +
                "unlocked_at BIGINT DEFAULT 0, " +
                "PRIMARY KEY (uuid, achievement)" +
                ")");

        // Tabla de partidas
        stmt.execute("CREATE TABLE IF NOT EXISTS " + prefix + "games (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "arena VARCHAR(32) NOT NULL, " +
                "theme VARCHAR(64) NOT NULL, " +
                "winner VARCHAR(36), " +
                "winner_name VARCHAR(16), " +
                "points INT DEFAULT 0, " +
                "played_at BIGINT DEFAULT 0, " +
                "mode VARCHAR(32) DEFAULT 'NORMAL'" +
                ")");

        // Tabla de galeria
        stmt.execute("CREATE TABLE IF NOT EXISTS " + prefix + "gallery (" +
                "id VARCHAR(8) PRIMARY KEY, " +
                "uuid VARCHAR(36) NOT NULL, " +
                "player_name VARCHAR(16) NOT NULL, " +
                "theme VARCHAR(64) NOT NULL, " +
                "votes INT DEFAULT 0, " +
                "likes INT DEFAULT 0, " +
                "created_at BIGINT DEFAULT 0" +
                ")");

        // Tabla de amigos
        stmt.execute("CREATE TABLE IF NOT EXISTS " + prefix + "friends (" +
                "uuid VARCHAR(36) NOT NULL, " +
                "friend_uuid VARCHAR(36) NOT NULL, " +
                "since BIGINT DEFAULT 0, " +
                "PRIMARY KEY (uuid, friend_uuid)" +
                ")");

        // Tabla de misiones
        stmt.execute("CREATE TABLE IF NOT EXISTS " + prefix + "missions (" +
                "uuid VARCHAR(36) NOT NULL, " +
                "date VARCHAR(16) NOT NULL, " +
                "mission VARCHAR(128) NOT NULL, " +
                "completed BOOLEAN DEFAULT FALSE, " +
                "PRIMARY KEY (uuid, date, mission)" +
                ")");

        // Tabla de torneo
        stmt.execute("CREATE TABLE IF NOT EXISTS " + prefix + "tournament (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "player_name VARCHAR(16) NOT NULL, " +
                "score INT DEFAULT 0, " +
                "season INT DEFAULT 1" +
                ")");

        stmt.close();
        plugin.getLogger().info("[APB] Tablas MySQL creadas correctamente.");
    }

    private void startKeepAlive() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (connection == null || connection.isClosed()) {
                        plugin.getLogger().warning("[APB] MySQL desconectado. Reconectando...");
                        connect();
                    } else {
                        connection.createStatement().execute("SELECT 1");
                    }
                } catch (SQLException e) {
                    plugin.getLogger().warning("[APB] MySQL keepalive error: " + e.getMessage());
                }
            }
        }.runTaskTimerAsynchronously(plugin, 6000L, 6000L);
    }

    // STATS
    public void saveStats(UUID uuid, String name, int wins, int games, int points, int coins, String rank) {
        if (!enabled) return;
        runAsync(() -> {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO " + prefix + "stats (uuid, name, wins, games, points, coins, rank_name, last_seen) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE name=?, wins=?, games=?, points=?, coins=?, rank_name=?, last_seen=?");
                long now = System.currentTimeMillis();
                stmt.setString(1, uuid.toString());
                stmt.setString(2, name);
                stmt.setInt(3, wins);
                stmt.setInt(4, games);
                stmt.setInt(5, points);
                stmt.setInt(6, coins);
                stmt.setString(7, rank);
                stmt.setLong(8, now);
                stmt.setString(9, name);
                stmt.setInt(10, wins);
                stmt.setInt(11, games);
                stmt.setInt(12, points);
                stmt.setInt(13, coins);
                stmt.setString(14, rank);
                stmt.setLong(15, now);
                stmt.executeUpdate();
                stmt.close();
            } catch (SQLException e) {
                plugin.getLogger().warning("[APB] Error guardando stats MySQL: " + e.getMessage());
            }
        });
    }

    public Map<String, Integer> getTopPlayers(int limit) {
        Map<String, Integer> map = new LinkedHashMap<>();
        if (!enabled) return map;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT name, points FROM " + prefix + "stats ORDER BY points DESC LIMIT ?");
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("name"), rs.getInt("points"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().warning("[APB] Error obteniendo top players: " + e.getMessage());
        }
        return map;
    }

    // LOGROS
    public void saveAchievement(UUID uuid, String achievement) {
        if (!enabled) return;
        runAsync(() -> {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT IGNORE INTO " + prefix + "achievements (uuid, achievement, unlocked_at) VALUES (?, ?, ?)");
                stmt.setString(1, uuid.toString());
                stmt.setString(2, achievement);
                stmt.setLong(3, System.currentTimeMillis());
                stmt.executeUpdate();
                stmt.close();
            } catch (SQLException e) {
                plugin.getLogger().warning("[APB] Error guardando logro MySQL: " + e.getMessage());
            }
        });
    }

    public List<String> getAchievements(UUID uuid) {
        List<String> list = new ArrayList<>();
        if (!enabled) return list;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT achievement FROM " + prefix + "achievements WHERE uuid = ?");
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(rs.getString("achievement"));
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().warning("[APB] Error obteniendo logros MySQL: " + e.getMessage());
        }
        return list;
    }

    // PARTIDAS
    public void saveGame(String arena, String theme, String winnerUUID,
                          String winnerName, int points, String mode) {
        if (!enabled) return;
        runAsync(() -> {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO " + prefix + "games (arena, theme, winner, winner_name, points, played_at, mode) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)");
                stmt.setString(1, arena);
                stmt.setString(2, theme);
                stmt.setString(3, winnerUUID);
                stmt.setString(4, winnerName);
                stmt.setInt(5, points);
                stmt.setLong(6, System.currentTimeMillis());
                stmt.setString(7, mode);
                stmt.executeUpdate();
                stmt.close();
            } catch (SQLException e) {
                plugin.getLogger().warning("[APB] Error guardando partida MySQL: " + e.getMessage());
            }
        });
    }

    public List<Map<String, Object>> getGameHistory(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (!enabled) return list;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM " + prefix + "games ORDER BY played_at DESC LIMIT ?");
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("arena", rs.getString("arena"));
                entry.put("theme", rs.getString("theme"));
                entry.put("winner", rs.getString("winner_name"));
                entry.put("points", rs.getInt("points"));
                entry.put("mode", rs.getString("mode"));
                entry.put("date", rs.getLong("played_at"));
                list.add(entry);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().warning("[APB] Error obteniendo historial: " + e.getMessage());
        }
        return list;
    }

    // TORNEO
    public void saveTournamentScore(UUID uuid, String name, int score, int season) {
        if (!enabled) return;
        runAsync(() -> {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO " + prefix + "tournament (uuid, player_name, score, season) " +
                        "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE score=?, player_name=?");
                stmt.setString(1, uuid.toString());
                stmt.setString(2, name);
                stmt.setInt(3, score);
                stmt.setInt(4, season);
                stmt.setInt(5, score);
                stmt.setString(6, name);
                stmt.executeUpdate();
                stmt.close();
            } catch (SQLException e) {
                plugin.getLogger().warning("[APB] Error guardando torneo MySQL: " + e.getMessage());
            }
        });
    }

    // UTILIDADES
    private void runAsync(Runnable task) {
        new BukkitRunnable() {
            @Override
            public void run() { task.run(); }
        }.runTaskAsynchronously(plugin);
    }

    public boolean isEnabled() { return enabled; }
    public Connection getConnection() { return connection; }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                plugin.getLogger().info("[APB] MySQL desconectado correctamente.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
