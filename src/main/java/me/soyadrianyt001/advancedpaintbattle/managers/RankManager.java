package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GamePlayer;
import me.soyadrianyt001.advancedpaintbattle.utils.EffectUtil;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RankManager {

    private final AdvancedPaintBattle plugin;
    private File rankFile;
    private FileConfiguration rankConfig;

    public RankManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        rankFile = new File(plugin.getDataFolder(), "ranks.yml");
        if (!rankFile.exists()) try { rankFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        rankConfig = YamlConfiguration.loadConfiguration(rankFile);
    }

    public String getRank(UUID uuid) { return rankConfig.getString(uuid + ".rank", "§7Aprendiz"); }

    public void checkRankUp(Player player, GamePlayer gp) {
        int totalPoints = plugin.getStatsManager().getTotalPoints(player.getUniqueId()) + gp.getPoints();
        String newRank = calculateRank(totalPoints);
        String current = getRank(player.getUniqueId());
        if (!newRank.equals(current)) {
            rankConfig.set(player.getUniqueId() + ".rank", newRank);
            save();
            player.sendTitle("§6§l⬆ RANGO SUBIDO", "§eAhora eres " + newRank, 10, 60, 10);
            plugin.getMessageManager().send(player, "rank-up", "%rank%", newRank);
            EffectUtil.spawnRankUpParticles(player);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
        }
    }

    private String calculateRank(int points) {
        if (points >= 15000) return "§6§lLeyenda";
        if (points >= 5000) return "§bMaestro";
        if (points >= 1000) return "§aArtista";
        return "§7Aprendiz";
    }

    private void save() {
        try { rankConfig.save(rankFile); } catch (IOException e) { e.printStackTrace(); }
    }
}
