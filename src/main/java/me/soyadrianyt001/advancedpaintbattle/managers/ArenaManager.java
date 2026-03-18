package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.Arena;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaManager {

    private final AdvancedPaintBattle plugin;
    private final Map<String, Arena> arenas = new HashMap<>();
    private File arenasFile;
    private FileConfiguration arenasConfig;

    public ArenaManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        loadArenas();
    }

    private void loadArenas() {
        arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
        if (!arenasFile.exists()) {
            try { arenasFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        if (arenasConfig.getConfigurationSection("arenas") != null) {
            for (String key : arenasConfig.getConfigurationSection("arenas").getKeys(false)) {
                Arena arena = new Arena(key);
                if (arenasConfig.contains("arenas." + key + ".lobby")) {
                    arena.setLobby((Location) arenasConfig.get("arenas." + key + ".lobby"));
                }
                if (arenasConfig.contains("arenas." + key + ".canvas")) {
                    arena.setCanvasOrigin((Location) arenasConfig.get("arenas." + key + ".canvas"));
                }
                arenas.put(key, arena);
            }
        }
    }

    public void saveArenas() {
        for (Map.Entry<String, Arena> entry : arenas.entrySet()) {
            String path = "arenas." + entry.getKey();
            Arena arena = entry.getValue();
            if (arena.getLobby() != null) arenasConfig.set(path + ".lobby", arena.getLobby());
            if (arena.getCanvasOrigin() != null) arenasConfig.set(path + ".canvas", arena.getCanvasOrigin());
        }
        try { arenasConfig.save(arenasFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public Arena createArena(String name) {
        Arena arena = new Arena(name);
        arenas.put(name, arena);
        saveArenas();
        return arena;
    }

    public boolean deleteArena(String name) {
        if (!arenas.containsKey(name)) return false;
        arenas.remove(name);
        arenasConfig.set("arenas." + name, null);
        try { arenasConfig.save(arenasFile); } catch (IOException e) { e.printStackTrace(); }
        return true;
    }

    public Arena getArena(String name) { return arenas.get(name); }
    public Map<String, Arena> getArenas() { return arenas; }

    public Arena getAvailableArena() {
        return arenas.values().stream()
                .filter(a -> a.getState() == Arena.ArenaState.WAITING && a.isReady() && !a.isFull())
                .findFirst().orElse(null);
    }

    public boolean arenaExists(String name) { return arenas.containsKey(name); }
}
