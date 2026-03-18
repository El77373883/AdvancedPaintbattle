package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GamePlayer;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class MissionManager {

    private final AdvancedPaintBattle plugin;
    private File file;
    private FileConfiguration config;

    private static final List<String> MISSION_TEMPLATES = Arrays.asList(
            "Juega 3 partidas",
            "Gana 1 partida",
            "Recibe 5 votos",
            "Vota en 3 partidas",
            "Consigue 100 puntos",
            "Usa el pincel arcoiris",
            "Usa el balde de relleno 5 veces"
    );

    public MissionManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "missions.yml");
        if (!file.exists()) try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public List<String> getMissions(UUID uuid) {
        String today = LocalDate.now().toString();
        String path = uuid + "." + today;
        if (!config.contains(path)) {
            Collections.shuffle(MISSION_TEMPLATES);
            List<String> daily = MISSION_TEMPLATES.subList(0, Math.min(3, MISSION_TEMPLATES.size()));
            config.set(path, daily);
            save();
        }
        return config.getStringList(path);
    }

    public void checkMissions(Player player, GameSession session, GamePlayer gp) {
        if (gp.getGamesWon() > 0) plugin.getAchievementManager().unlock(player, "FIRST_WIN");
        if (plugin.getStatsManager().getWins(player.getUniqueId()) >= 10) plugin.getAchievementManager().unlock(player, "WINS_10");
        if (plugin.getStatsManager().getGamesPlayed(player.getUniqueId()) >= 10) plugin.getAchievementManager().unlock(player, "GAMES_10");
    }

    private void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
