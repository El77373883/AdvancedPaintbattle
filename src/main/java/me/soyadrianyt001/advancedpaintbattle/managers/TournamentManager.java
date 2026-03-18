package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TournamentManager {

    private final AdvancedPaintBattle plugin;
    private File file;
    private FileConfiguration config;
    private boolean tournamentActive = false;
    private List<UUID> participants = new ArrayList<>();
    private Map<UUID, Integer> scores = new HashMap<>();

    public TournamentManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "tournament.yml");
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void startTournament() {
        tournamentActive = true;
        participants.clear();
        scores.clear();
        Bukkit.broadcastMessage("§6§l╔══════════════════════════════════╗");
        Bukkit.broadcastMessage("§6§l║  §e§l¡TORNEO SEMANAL INICIADO!      §6§l║");
        Bukkit.broadcastMessage("§6§l║  §7Usa §e/apb join §7para participar  §6§l║");
        Bukkit.broadcastMessage("§6§l╚══════════════════════════════════╝");
        plugin.getDiscordManager().announceTournament();
    }

    public void endTournament() {
        tournamentActive = false;
        announceWinners();
        rewardWinners();
        save();
    }

    private void announceWinners() {
        List<Map.Entry<UUID, Integer>> sorted = getSortedScores();
        Bukkit.broadcastMessage("§6§l╔══════ RESULTADO TORNEO ══════╗");
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            Player p = Bukkit.getPlayer(sorted.get(i).getKey());
            String name = p != null ? p.getName() : "Desconocido";
            String medal = i == 0 ? "§6🥇" : i == 1 ? "§7🥈" : "§c🥉";
            Bukkit.broadcastMessage(medal + " §f" + name + " §7- §e" + sorted.get(i).getValue() + " pts");
        }
        Bukkit.broadcastMessage("§6§l╚══════════════════════════════╝");
    }

    private void rewardWinners() {
        List<Map.Entry<UUID, Integer>> sorted = getSortedScores();
        int[] coinRewards = {500, 300, 150};
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            Player p = Bukkit.getPlayer(sorted.get(i).getKey());
            if (p != null) {
                plugin.getCoinManager().addCoins(p, coinRewards[i]);
                p.sendMessage("§6§l★ §e¡Ganaste §6" + coinRewards[i] + " monedas §epor el torneo!");
                if (i == 0) {
                    p.sendMessage("§6§l★ §e¡Eres el campeon del torneo! ¡Felicidades!");
                    plugin.getAchievementManager().unlock(p, "TOURNAMENT_WIN");
                }
            }
        }
    }

    private List<Map.Entry<UUID, Integer>> getSortedScores() {
        List<Map.Entry<UUID, Integer>> sorted = new ArrayList<>(scores.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());
        return sorted;
    }

    public boolean isActive() { return tournamentActive; }

    public void addScore(UUID uuid, int points) {
        scores.merge(uuid, points, Integer::sum);
        config.set("scores." + uuid, scores.get(uuid));
        save();
    }

    public int getScore(UUID uuid) {
        return scores.getOrDefault(uuid, 0);
    }

    public int getPosition(UUID uuid) {
        List<Map.Entry<UUID, Integer>> sorted = getSortedScores();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getKey().equals(uuid)) return i + 1;
        }
        return -1;
    }

    public void addParticipant(UUID uuid) {
        if (!participants.contains(uuid)) {
            participants.add(uuid);
            scores.putIfAbsent(uuid, 0);
        }
    }

    public boolean isParticipant(UUID uuid) {
        return participants.contains(uuid);
    }

    public int getTotalParticipants() {
        return participants.size();
    }

    public List<Map.Entry<UUID, Integer>> getLeaderboard(int limit) {
        List<Map.Entry<UUID, Integer>> sorted = getSortedScores();
        return sorted.subList(0, Math.min(limit, sorted.size()));
    }

    private void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}

