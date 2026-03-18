package me.soyadrianyt001.advancedpaintbattle.modes;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GamePlayer;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import me.soyadrianyt001.advancedpaintbattle.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamMode extends GameMode {

    private final Map<String, List<UUID>> teams = new HashMap<>();
    private final Map<UUID, String> playerTeam = new HashMap<>();
    private static final String[] TEAM_NAMES = {"§cEquipo Rojo", "§9Equipo Azul", "§aEquipo Verde"};
    private static final String[] TEAM_KEYS = {"RED", "BLUE", "GREEN"};

    public TeamMode(AdvancedPaintBattle plugin, GameSession session) {
        super(plugin, session);
    }

    @Override
    public void onStart() {
        assignTeams();
        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null) return;
            String team = playerTeam.get(gp.getUuid());
            String teamName = getTeamDisplayName(team);
            p.sendTitle("§e§l¡MODO EQUIPOS!", "§7Eres del " + teamName, 10, 60, 10);
            p.sendMessage("§e§l★ §7Eres del " + teamName + "§7!");
            p.sendMessage("§e§l★ §7Pinta junto a tu equipo. ¡El equipo con mas votos gana!");
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
        });
        teleportTeams();
    }

    private void assignTeams() {
        teams.clear();
        playerTeam.clear();
        for (String key : TEAM_KEYS) teams.put(key, new ArrayList<>());

        List<GamePlayer> players = new ArrayList<>(session.getPlayers());
        Collections.shuffle(players);

        for (int i = 0; i < players.size(); i++) {
            String teamKey = TEAM_KEYS[i % TEAM_KEYS.length];
            teams.get(teamKey).add(players.get(i).getUuid());
            playerTeam.put(players.get(i).getUuid(), teamKey);
        }
    }

    private void teleportTeams() {
        for (int t = 0; t < TEAM_KEYS.length; t++) {
            String key = TEAM_KEYS[t];
            List<UUID> members = teams.get(key);
            Location canvasLoc = null;
            // Obtener canvas del primer miembro del equipo
            for (UUID uuid : members) {
                canvasLoc = session.getCanvasLocations().get(uuid);
                if (canvasLoc != null) break;
            }
            if (canvasLoc == null) continue;
            for (UUID uuid : members) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    p.teleport(canvasLoc.clone().add(
                            plugin.getConfigManager().getCanvasSize() / 2.0, 1,
                            plugin.getConfigManager().getCanvasSize() / 2.0));
                    // Compartir canvas con todo el equipo
                    session.getCanvasLocations().put(uuid, canvasLoc);
                }
            }
        }
    }

    @Override
    public void onTick(int timeLeft) {
        if (timeLeft == 30) {
            ChatUtil.sendArenaMessage(plugin, session.getArenaName(),
                    "§e§l★ §e¡30 segundos! ¡Coordinen con su equipo!");
        }
    }

    @Override
    public void onEnd() {
        announceTeamResults();
        teams.clear();
        playerTeam.clear();
    }

    private void announceTeamResults() {
        Map<String, Integer> teamVotes = new HashMap<>();
        for (String key : TEAM_KEYS) teamVotes.put(key, 0);

        session.getPlayers().forEach(gp -> {
            String team = playerTeam.get(gp.getUuid());
            if (team != null) {
                teamVotes.merge(team, gp.getVotes(), Integer::sum);
            }
        });

        String winnerTeam = teamVotes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);

        if (winnerTeam == null) return;
        String winnerName = getTeamDisplayName(winnerTeam);
        Bukkit.broadcastMessage("§6§l★ §e¡El " + winnerName + " §egano el modo equipos!");

        // Bonus de puntos al equipo ganador
        for (UUID uuid : teams.get(winnerTeam)) {
            GamePlayer gp = session.getGamePlayer(uuid);
            if (gp != null) gp.addPoints(100);
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.sendMessage("§6§l★ §e¡Tu equipo gano! +100 puntos bonus!");
                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
            }
        }
    }

    public String getTeamDisplayName(String key) {
        for (int i = 0; i < TEAM_KEYS.length; i++) {
            if (TEAM_KEYS[i].equals(key)) return TEAM_NAMES[i];
        }
        return "§7Desconocido";
    }

    public String getPlayerTeam(UUID uuid) { return playerTeam.get(uuid); }
    public List<UUID> getTeamMembers(String team) { return teams.getOrDefault(team, new ArrayList<>()); }
    public boolean areTeammates(UUID a, UUID b) {
        String teamA = playerTeam.get(a);
        String teamB = playerTeam.get(b);
        return teamA != null && teamA.equals(teamB);
    }

    @Override
    public String getName() { return "TEAM"; }

    @Override
    public String getDisplayName() { return "§e§lModo Equipos"; }

    @Override
    public String getDescription() { return "§72v2v2 - Equipos pintan juntos"; }
}
