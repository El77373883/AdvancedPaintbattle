package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.utils.FileUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class DataManager {

    private final AdvancedPaintBattle plugin;
    private final FileUtil fileUtil;
    private FileConfiguration dataConfig;

    public DataManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        this.fileUtil = new FileUtil(plugin);
        load();
        startAutoBackup();
    }

    private void load() {
        dataConfig = fileUtil.loadConfig("data.yml");
        initDefaults();
    }

    private void initDefaults() {
        if (!dataConfig.contains("server.totalGames")) dataConfig.set("server.totalGames", 0);
        if (!dataConfig.contains("server.totalPlayers")) dataConfig.set("server.totalPlayers", 0);
        if (!dataConfig.contains("server.startDate")) dataConfig.set("server.startDate", System.currentTimeMillis());
        if (!dataConfig.contains("season.current")) dataConfig.set("season.current", 1);
        if (!dataConfig.contains("season.startDate")) dataConfig.set("season.startDate", System.currentTimeMillis());
        save();
    }

    private void startAutoBackup() {
        if (!plugin.getConfig().getBoolean("backup.enabled", true)) return;
        int interval = plugin.getConfig().getInt("backup.interval", 3600);
        new BukkitRunnable() {
            @Override
            public void run() {
                fileUtil.backupAll();
                plugin.getLogger().info("[APB] Backup automatico completado.");
            }
        }.runTaskTimerAsynchronously(plugin, interval * 20L, interval * 20L);
    }

    public void save() {
        fileUtil.saveConfig(dataConfig, "data.yml");
    }

    public void saveAll() {
        save();
        plugin.getPlayerDataManager().save();
        plugin.getStatsManager().saveAll();
        fileUtil.backupAll();
        plugin.getLogger().info("[APB] Todos los datos guardados.");
    }

    // ESTADISTICAS GLOBALES
    public int getTotalGames() { return dataConfig.getInt("server.totalGames", 0); }
    public void incrementTotalGames() {
        dataConfig.set("server.totalGames", getTotalGames() + 1);
        save();
    }

    public int getTotalPlayers() { return dataConfig.getInt("server.totalPlayers", 0); }
    public void incrementTotalPlayers() {
        dataConfig.set("server.totalPlayers", getTotalPlayers() + 1);
        save();
    }

    // TEMPORADA
    public int getCurrentSeason() { return dataConfig.getInt("season.current", 1); }
    public void nextSeason() {
        int season = getCurrentSeason();
        dataConfig.set("season.current", season + 1);
        dataConfig.set("season.startDate", System.currentTimeMillis());
        save();
        plugin.getLogger().info("[APB] ¡Nueva temporada iniciada: " + (season + 1) + "!");
    }

    // HALL OF FAME
    public void setHallOfFame(String playerName, int points) {
        dataConfig.set("halloffame." + playerName + ".points", points);
        dataConfig.set("halloffame." + playerName + ".date", System.currentTimeMillis());
        save();
    }

    public FileConfiguration getConfig() { return dataConfig; }
}
