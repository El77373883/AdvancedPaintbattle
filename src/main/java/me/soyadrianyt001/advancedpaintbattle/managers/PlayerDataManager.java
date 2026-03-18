package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.utils.FileUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayerDataManager {

    private final AdvancedPaintBattle plugin;
    private final FileUtil fileUtil;
    private FileConfiguration playersConfig;

    public PlayerDataManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        this.fileUtil = new FileUtil(plugin);
        load();
        startAutoSave();
    }

    private void load() {
        playersConfig = fileUtil.loadConfig("players.yml");
    }

    public void save() {
        fileUtil.saveConfig(playersConfig, "players.yml");
    }

    private void startAutoSave() {
        int interval = plugin.getConfigManager().getAutoSaveInterval();
        new BukkitRunnable() {
            @Override
            public void run() {
                save();
                plugin.getLogger().info("[APB] Datos de jugadores guardados automaticamente.");
            }
        }.runTaskTimerAsynchronously(plugin, interval * 20L, interval * 20L);
    }

    // GUARDAR datos al salir
    public void savePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid;
        playersConfig.set(path + ".name", player.getName());
        playersConfig.set(path + ".lastSeen", System.currentTimeMillis());
        playersConfig.set(path + ".wins", plugin.getStatsManager().getWins(uuid));
        playersConfig.set(path + ".games", plugin.getStatsManager().getGamesPlayed(uuid));
        playersConfig.set(path + ".points", plugin.getStatsManager().getTotalPoints(uuid));
        playersConfig.set(path + ".coins", plugin.getCoinManager().getCoins(uuid));
        playersConfig.set(path + ".rank", plugin.getRankManager().getRank(uuid));
        save();
    }

    // CARGAR datos al entrar
    public void loadPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid;
        if (!playersConfig.contains(path)) {
            // Jugador nuevo
            playersConfig.set(path + ".name", player.getName());
            playersConfig.set(path + ".firstJoin", System.currentTimeMillis());
            playersConfig.set(path + ".coins", 0);
            playersConfig.set(path + ".rank", "§7Aprendiz");
            save();
            plugin.getLogger().info("[APB] Nuevo jugador registrado: " + player.getName());
        }
    }

    public boolean isRegistered(UUID uuid) {
        return playersConfig.contains("players." + uuid);
    }

    public long getLastSeen(UUID uuid) {
        return playersConfig.getLong("players." + uuid + ".lastSeen", 0);
    }

    public long getFirstJoin(UUID uuid) {
        return playersConfig.getLong("players." + uuid + ".firstJoin", 0);
    }

    public int getTotalPlayers() {
        if (playersConfig.getConfigurationSection("players") == null) return 0;
        return playersConfig.getConfigurationSection("players").getKeys(false).size();
    }

    public FileConfiguration getConfig() { return playersConfig; }
}
