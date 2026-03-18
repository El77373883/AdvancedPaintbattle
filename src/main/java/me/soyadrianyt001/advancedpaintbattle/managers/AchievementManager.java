package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.utils.EffectUtil;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AchievementManager {

    private final AdvancedPaintBattle plugin;
    private File file;
    private FileConfiguration config;

    private static final Map<String, String> ALL_ACHIEVEMENTS = new LinkedHashMap<>();

    static {
        ALL_ACHIEVEMENTS.put("FIRST_WIN", "§6Primera Victoria");
        ALL_ACHIEVEMENTS.put("WINS_10", "§610 Victorias");
        ALL_ACHIEVEMENTS.put("WINS_50", "§650 Victorias");
        ALL_ACHIEVEMENTS.put("VOTES_FIRST", "§ePrimer Voto Recibido");
        ALL_ACHIEVEMENTS.put("VOTES_100", "§e100 Votos Recibidos");
        ALL_ACHIEVEMENTS.put("GAMES_10", "§a10 Partidas Jugadas");
        ALL_ACHIEVEMENTS.put("GAMES_100", "§a100 Partidas Jugadas");
        ALL_ACHIEVEMENTS.put("RANK_ARTISTA", "§aAlcanza Artista");
        ALL_ACHIEVEMENTS.put("RANK_MAESTRO", "§bAlcanza Maestro");
        ALL_ACHIEVEMENTS.put("RANK_LEYENDA", "§6Alcanza Leyenda");
        ALL_ACHIEVEMENTS.put("COINS_1000", "§61000 Monedas");
        ALL_ACHIEVEMENTS.put("PERFECT_VOTE", "§dVotos Perfectos");
    }

    public AchievementManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "achievements.yml");
        if (!file.exists()) try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void unlock(Player player, String achievementKey) {
        String path = player.getUniqueId() + "." + achievementKey;
        if (config.getBoolean(path, false)) return;
        config.set(path, true);
        save();
        String name = ALL_ACHIEVEMENTS.getOrDefault(achievementKey, achievementKey);
        player.sendTitle("§d§l🏆 LOGRO", "§e" + name, 10, 60, 10);
        plugin.getMessageManager().send(player, "achievement-unlocked", "%achievement%", name);
        EffectUtil.spawnAchievementParticles(player);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
    }

    public List<String> getAchievements(UUID uuid) {
        List<String> list = new ArrayList<>();
        for (String key : ALL_ACHIEVEMENTS.keySet()) {
            if (config.getBoolean(uuid + "." + key, false)) {
                list.add(ALL_ACHIEVEMENTS.get(key));
            }
        }
        return list;
    }

    public boolean hasAchievement(UUID uuid, String key) {
        return config.getBoolean(uuid + "." + key, false);
    }

    private void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
