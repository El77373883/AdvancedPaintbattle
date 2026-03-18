package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class CoinManager {

    private final AdvancedPaintBattle plugin;
    private File coinsFile;
    private FileConfiguration coinsConfig;

    public CoinManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        coinsFile = new File(plugin.getDataFolder(), "coins.yml");
        if (!coinsFile.exists()) try { coinsFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        coinsConfig = YamlConfiguration.loadConfiguration(coinsFile);
    }

    public int getCoins(UUID uuid) { return coinsConfig.getInt(uuid.toString(), 0); }

    public void addCoins(Player player, int amount) {
        int current = getCoins(player.getUniqueId());
        coinsConfig.set(player.getUniqueId().toString(), current + amount);
        save();
        if (amount > 0) plugin.getMessageManager().send(player, "coins-received", "%coins%", String.valueOf(amount));
    }

    public boolean removeCoins(Player player, int amount) {
        int current = getCoins(player.getUniqueId());
        if (current < amount) return false;
        coinsConfig.set(player.getUniqueId().toString(), current - amount);
        save();
        return true;
    }

    public void setCoins(UUID uuid, int amount) {
        coinsConfig.set(uuid.toString(), amount);
        save();
    }

    private void save() {
        try { coinsConfig.save(coinsFile); } catch (IOException e) { e.printStackTrace(); }
    }
}
