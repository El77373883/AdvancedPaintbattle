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
        if (!file.exists()) try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
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
        save();
    }

    private void announceWinners() {
        List<Map.Entry<UUID, Integer>> sorted = new ArrayList<>(scores.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());
        Bukkit.broadcastMessage("§6§l╔══════ RESULTADO TORNEO ══════╗");
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            Player p = Bukkit.getPlayer(sorted.get(i).getKey());
            String name = p != null ? p.getName() : "Desconocido";
            String medal = i == 0 ? "§6🥇" : i == 1 ? "§7🥈" : "§c🥉";
            Bukkit.broadcastMessage(medal + " §f" + name + " §7- §e" + sorted.get(i).getValue() + " pts");
        }
        Bukkit.broadcastMessage("§6§l╚══════════════════════════╝");
    }

    public boolean isActive() { return tournamentActive; }
    public void addScore(UUID uuid, int points) { scores.merge(uuid, points, Integer::sum); }

    private void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
