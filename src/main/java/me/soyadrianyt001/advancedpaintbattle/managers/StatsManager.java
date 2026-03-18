package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GamePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StatsManager {

    private final AdvancedPaintBattle plugin;
    private File statsFile;
    private FileConfiguration statsConfig;

    public StatsManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        statsFile = new File(plugin.getDataFolder(), "stats.yml");
        if (!statsFile.exists()) try { statsFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        statsConfig = YamlConfiguration.loadConfiguration(statsFile);
    }

    public void addStats(GamePlayer gp) {
        String path = "players." + gp.getUuid();
        statsConfig.set(path + ".name", gp.getName());
        statsConfig.set(path + ".games", getGamesPlayed(gp.getUuid()) + 1);
        statsConfig.set(path + ".wins", getWins(gp.getUuid()) + gp.getGamesWon());
        statsConfig.set(path + ".points", getTotalPoints(gp.getUuid()) + gp.getPoints());
        save();
    }

    public void saveAll() { save(); }

    private void save() {
        try { statsConfig.save(statsFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public int getGamesPlayed(UUID uuid) { return statsConfig.getInt("players." + uuid + ".games", 0); }
    public int getWins(UUID uuid) { return statsConfig.getInt("players." + uuid + ".wins", 0); }
    public int getTotalPoints(UUID uuid) { return statsConfig.getInt("players." + uuid + ".points", 0); }

    public List<Map.Entry<String, Integer>> getTopPlayers(int limit) {
        Map<String, Integer> map = new HashMap<>();
        if (statsConfig.getConfigurationSection("players") == null) return new ArrayList<>();
        for (String uuid : statsConfig.getConfigurationSection("players").getKeys(false)) {
            String name = statsConfig.getString("players." + uuid + ".name", uuid);
            int points = statsConfig.getInt("players." + uuid + ".points", 0);
            map.put(name, points);
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        return list.subList(0, Math.min(limit, list.size()));
    }
}
