package me.soyadrianyt001.advancedpaintbattle.utils;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {

    private final AdvancedPaintBattle plugin;

    public FileUtil(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    public File getOrCreateFile(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("[APB] Error creando archivo: " + name);
                e.printStackTrace();
            }
        }
        return file;
    }

    public FileConfiguration loadConfig(String name) {
        return YamlConfiguration.loadConfiguration(getOrCreateFile(name));
    }

    public void saveConfig(FileConfiguration config, String name) {
        try {
            config.save(getOrCreateFile(name));
        } catch (IOException e) {
            plugin.getLogger().severe("[APB] Error guardando archivo: " + name);
            e.printStackTrace();
        }
    }

    public void backupFile(String name) {
        File original = new File(plugin.getDataFolder(), name);
        if (!original.exists()) return;
        File backupDir = new File(plugin.getDataFolder(), "backups");
        backupDir.mkdirs();
        String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File backup = new File(backupDir, name.replace(".yml", "") + "_" + date + ".yml");
        try {
            Files.copy(original.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            plugin.getLogger().info("[APB] Backup creado: " + backup.getName());
        } catch (IOException e) {
            plugin.getLogger().warning("[APB] Error creando backup: " + e.getMessage());
        }
    }

    public void backupAll() {
        String[] files = {"stats.yml", "coins.yml", "ranks.yml", "achievements.yml",
                "players.yml", "data.yml", "arenas.yml"};
        for (String f : files) backupFile(f);
    }
}
