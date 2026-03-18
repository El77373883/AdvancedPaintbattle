package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class BattlePassManager {

    private final AdvancedPaintBattle plugin;
    private File file;
    private FileConfiguration config;

    public BattlePassManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "battlepass.yml");
        if (!file.exists()) try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public int getXP(UUID uuid) { return config.getInt(uuid + ".xp", 0); }
    public int getLevel(UUID uuid) { return config.getInt(uuid + ".level", 1); }

    public void addXP(Player player, int xp) {
        int current = getXP(player.getUniqueId());
        int newXP = current + xp;
        int level = getLevel(player.getUniqueId());
        int required = level * 100;
        if (newXP >= required) {
            newXP -= required;
            level++;
            config.set(player.getUniqueId() + ".level", level);
            player.sendMessage("§6§l★ §ePase de Batalla: §6¡Nivel " + level + "!");
            plugin.getCoinManager().addCoins(player, 50);
        }
        config.set(player.getUniqueId() + ".xp", newXP);
        save();
    }

    private void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
