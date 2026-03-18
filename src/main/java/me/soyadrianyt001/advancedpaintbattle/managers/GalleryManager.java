package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GalleryManager {

    private final AdvancedPaintBattle plugin;
    private File file;
    private FileConfiguration config;

    public GalleryManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "gallery.yml");
        if (!file.exists()) try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveDrawing(UUID uuid, String playerName, String theme, int votes) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        String path = "gallery." + id;
        config.set(path + ".player", playerName);
        config.set(path + ".uuid", uuid.toString());
        config.set(path + ".theme", theme);
        config.set(path + ".votes", votes);
        config.set(path + ".likes", 0);
        config.set(path + ".date", System.currentTimeMillis());
        save();
    }

    public void addLike(String drawingId, UUID voter) {
        int likes = config.getInt("gallery." + drawingId + ".likes", 0);
        config.set("gallery." + drawingId + ".likes", likes + 1);
        save();
    }

    public List<Map<String, Object>> getTopDrawings(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (config.getConfigurationSection("gallery") == null) return list;
        for (String id : config.getConfigurationSection("gallery").getKeys(false)) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", id);
            entry.put("player", config.getString("gallery." + id + ".player"));
            entry.put("theme", config.getString("gallery." + id + ".theme"));
            entry.put("votes", config.getInt("gallery." + id + ".votes"));
            entry.put("likes", config.getInt("gallery." + id + ".likes"));
            list.add(entry);
        }
        list.sort((a, b) -> (int) b.get("likes") - (int) a.get("likes"));
        return list.subList(0, Math.min(limit, list.size()));
    }

    private void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
