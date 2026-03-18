package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.utils.FileUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminLogger {

    private final AdvancedPaintBattle plugin;
    private final FileUtil fileUtil;
    private FileConfiguration logConfig;

    public AdminLogger(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        this.fileUtil = new FileUtil(plugin);
        load();
    }

    private void load() {
        logConfig = fileUtil.loadConfig("admin_log.yml");
    }

    public void log(Player admin, String action) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String entry = "[" + date + "] " + admin.getName() + " -> " + action;

        List<String> logs = logConfig.getStringList("logs");
        logs.add(entry);

        // Mantener solo los ultimos 500 logs
        if (logs.size() > 500) logs = logs.subList(logs.size() - 500, logs.size());

        logConfig.set("logs", logs);
        fileUtil.saveConfig(logConfig, "admin_log.yml");

        plugin.getLogger().info("[APB-ADMIN] " + entry);
    }

    public void log(String action) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String entry = "[" + date + "] CONSOLE -> " + action;

        List<String> logs = logConfig.getStringList("logs");
        logs.add(entry);
        if (logs.size() > 500) logs = logs.subList(logs.size() - 500, logs.size());
        logConfig.set("logs", logs);
        fileUtil.saveConfig(logConfig, "admin_log.yml");
    }

    public List<String> getLogs(int limit) {
        List<String> logs = logConfig.getStringList("logs");
        if (logs.size() <= limit) return logs;
        return logs.subList(logs.size() - limit, logs.size());
    }

    public List<String> getLogsByAdmin(String adminName, int limit) {
        List<String> all = logConfig.getStringList("logs");
        List<String> filtered = new ArrayList<>();
        for (String log : all) {
            if (log.contains(adminName + " ->")) filtered.add(log);
        }
        if (filtered.size() <= limit) return filtered;
        return filtered.subList(filtered.size() - limit, filtered.size());
    }

    public void clearLogs() {
        logConfig.set("logs", new ArrayList<>());
        fileUtil.saveConfig(logConfig, "admin_log.yml");
    }
}
