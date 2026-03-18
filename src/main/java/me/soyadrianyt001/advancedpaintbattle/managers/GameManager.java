package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.*;
import me.soyadrianyt001.advancedpaintbattle.utils.EffectUtil;
import me.soyadrianyt001.advancedpaintbattle.utils.ScoreboardUtil;
import me.soyadrianyt001.advancedpaintbattle.utils.ThemeUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameManager {

    private final AdvancedPaintBattle plugin;
    private final Map<String, GameSession> sessions = new HashMap<>();
    private final Map<UUID, String> playerArena = new HashMap<>();
    private final Map<String, BukkitRunnable> timers = new HashMap<>();

    public GameManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    public boolean joinArena(Player player, String arenaName) {
        if (playerArena.containsKey(player.getUniqueId())) {
            plugin.getMessageManager().send(player, "already-in-game");
            return false;
        }
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null || !arena.isReady()) {
            plugin.getMessageManager().send(player, "no-arenas");
            return false;
        }
        if (arena.isFull()) {
            plugin.getMessageManager().send(player, "arena-full");
            return false;
        }

        GameSession session = sessions.computeIfAbsent(arenaName, GameSession::new);
        GamePlayer gp = new GamePlayer(player);
        session.getPlayers().add(gp);
        playerArena.put(player.getUniqueId(), arenaName);
        player.teleport(arena.getLobby());
        player.setGameMode(GameMode.ADVENTURE);

        broadcastToSession(session, plugin.getMessageManager().get("player-join-broadcast",
                "%player%", player.getName(),
                "%players%", String.valueOf(session.getPlayers().size()),
                "%max%", String.valueOf(arena.getMaxPlayers())));

        ScoreboardUtil.updateLobbyScoreboard(plugin, player, session, arena);

        if (session.getPlayers().size() >= arena.getMinPlayers() && session.getState() == GameSession.GameState.WAITING) {
            startCountdown(arenaName);
        }
        return true;
    }

    public void leaveArena(Player player) {
        String arenaName = playerArena.remove(player.getUniqueId());
        if (arenaName == null) return;
        GameSession session = sessions.get(arenaName);
        if (session == null) return;
        session.getPlayers().removeIf(gp -> gp.getUuid().equals(player.getUniqueId()));
        broadcastToSession(session, plugin.getMessageManager().get("player-leave-broadcast",
                "%player%", player.getName(),
                "%players%", String.valueOf(session.getPlayers().size()),
                "%max%", String.valueOf(plugin.getArenaManager().getArena(arenaName).getMaxPlayers())));
        ScoreboardUtil.removeScoreboard(player);
        player.setGameMode(GameMode.SURVIVAL);
        if (session.getPlayers().size() < plugin.getArenaManager().getArena(arenaName).getMinPlayers()) {
            if (session.getState() == GameSession.GameState.COUNTDOWN) {
                cancelTimer(arenaName);
                session.setState(GameSession.GameState.WAITING);
                broadcastToSession(session, "§c¡Faltan jugadores! Cuenta regresiva cancelada.");
            }
        }
    }

    private void startCountdown(String arenaName) {
        GameSession session = sessions.get(arenaName);
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        session.setState(GameSession.GameState.COUNTDOWN);
        int lobbyTime = plugin.getConfig().getInt("lobby-countdown", 30);
        session.setTimeLeft(lobbyTime);

        BukkitRunnable timer = new BukkitRunnable() {
            int time = lobbyTime;
            @Override
            public void run() {
                if (session.getPlayers().size() < arena.getMinPlayers()) {
                    cancel();
                    session.setState(GameSession.GameState.WAITING);
                    return;
                }
                session.setTimeLeft(time);
                session.getPlayers().forEach(gp -> {
                    Player p = Bukkit.getPlayer(gp.getUuid());
                    if (p == null) return;
                    ScoreboardUtil.updateLobbyScoreboard(plugin, p, session, arena);
                    if (time <= 5 && time > 0) {
                        p.sendTitle("§c§l" + time, "§ePreparate para pintar!", 5, 15, 5);
                        EffectUtil.playCountdownSound(p, time);
                        EffectUtil.spawnCountdownParticles(p);
                        plugin.getMessageManager().send(p, "start-countdown", "%time%", String.valueOf(time));
                    }
                });
                if (time <= 0) {
                    cancel();
                    startGame(arenaName);
                }
                time--;
            }
        };
        timer.runTaskTimer(plugin, 0L, 20L);
        timers.put(arenaName, timer);
    }

    public void startGame(String arenaName) {
        GameSession session = sessions.get(arenaName);
        session.setState(GameSession.GameState.PAINTING);
        session.nextRound();

        String theme = ThemeUtil.selectTheme(plugin, session);
        session.setCurrentTheme(theme);

        Arena arena = plugin.getArenaManager().getArena(arenaName);
        CanvasManager.buildCanvases(plugin, session, arena);

        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null) return;
            p.setGameMode(GameMode.SURVIVAL);
            p.sendTitle("§6§l✦ TEMA ✦", "§e§l" + theme, 10, 60, 10);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.5f);
            plugin.getMessageManager().send(p, "theme-announce", "%theme%", theme);
            GUIManager.givePalette(plugin, p);
            ScoreboardUtil.updateGameScoreboard(plugin, p, session, arena);
        });

        startPaintTimer(arenaName);
    }

    private void startPaintTimer(String arenaName) {
        GameSession session = sessions.get(arenaName);
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        int paintTime = plugin.getConfig().getInt("paint-time", 90);
        session.setTimeLeft(paintTime);

        BukkitRunnable timer = new BukkitRunnable() {
            int time = paintTime;
            @Override
            public void run() {
                session.setTimeLeft(time);
                session.getPlayers().forEach(gp -> {
                    Player p = Bukkit.getPlayer(gp.getUuid());
                    if (p == null) return;
                    ScoreboardUtil.updateGameScoreboard(plugin, p, session, arena);
                    if (time == 60 || time == 30 || time == 10) {
                        plugin.getMessageManager().send(p, "time-warning", "%time%", String.valueOf(time));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                    }
                    if (time <= 5 && time > 0) {
                        p.sendTitle("§c§l" + time, "§e¡Termina tu dibujo!", 5, 15, 5);
                        EffectUtil.playCountdownSound(p, time);
                    }
                });
                if (time <= 0) {
                    cancel();
                    startVoting(arenaName);
                }
                time--;
            }
        };
        timer.runTaskTimer(plugin, 0L, 20L);
        timers.put(arenaName + "_paint", timer);
    }

    public void startVoting(String arenaName) {
        GameSession session = sessions.get(arenaName);
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        session.setState(GameSession.GameState.VOTING);
        int voteTime = plugin.getConfig().getInt("vote-time", 25);
        session.setTimeLeft(voteTime);

        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null) return;
            plugin.getMessageManager().send(p, "paint-end");
            p.sendTitle("§c§lTIEMPO!", "§eAhora vota por el mejor", 10, 40, 10);
            GUIManager.openVotingGUI(plugin, p, session);
            ScoreboardUtil.updateVoteScoreboard(plugin, p, session, arena);
        });

        BukkitRunnable timer = new BukkitRunnable() {
            int time = voteTime;
            @Override
            public void run() {
                session.setTimeLeft(time);
                if (time <= 0) {
                    cancel();
                    endRound(arenaName);
                }
                time--;
            }
        };
        timer.runTaskTimer(plugin, 0L, 20L);
        timers.put(arenaName + "_vote", timer);
    }

    public void vote(Player voter, UUID targetUuid, String arenaName) {
        GameSession session = sessions.get(arenaName);
        if (session == null || session.getState() != GameSession.GameState.VOTING) return;
        GamePlayer gp = session.getGamePlayer(voter.getUniqueId());
        if (gp == null || gp.hasVoted()) {
            plugin.getMessageManager().send(voter, "already-voted");
            return;
        }
        GamePlayer target = session.getGamePlayer(targetUuid);
        if (target == null) return;
        gp.setHasVoted(true);
        gp.setVotedFor(targetUuid);
        target.addVote();
        int votePoints = plugin.getConfig().getInt("points-vote-received", 50);
        target.addPoints(votePoints);
        gp.addPoints(plugin.getConfig().getInt("points-voted", 10));
        plugin.getMessageManager().send(voter, "voted", "%player%", target.getName(), "%points%", String.valueOf(votePoints));
        EffectUtil.spawnVoteParticles(voter);
        voter.playSound(voter.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.5f);
        Player targetPlayer = Bukkit.getPlayer(targetUuid);
        if (targetPlayer != null) EffectUtil.spawnStarParticles(targetPlayer);
    }

    public void endRound(String arenaName) {
        GameSession session = sessions.get(arenaName);
        session.setState(GameSession.GameState.ROUND_END);
        List<GamePlayer> top = session.getTopPlayers();

        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null) return;
            p.sendMessage("§6§l══════ RESULTADO RONDA " + session.getCurrentRound() + " ══════");
            for (int i = 0; i < Math.min(3, top.size()); i++) {
                String medal = i == 0 ? "§6#1" : i == 1 ? "§7#2" : "§c#3";
                p.sendMessage(medal + " §f" + top.get(i).getName() + " §7- §e" + top.get(i).getPoints() + " pts");
            }
            p.sendMessage("§6§l══════════════════════════════");
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                if (session.isLastRound()) {
                    endGame(arenaName);
                } else {
                    session.nextRound();
                    String theme = ThemeUtil.selectTheme(plugin, session);
                    session.setCurrentTheme(theme);
                    session.getPlayers().forEach(gp -> gp.setHasVoted(false));
                    CanvasManager.clearCanvases(plugin, session, plugin.getArenaManager().getArena(arenaName));
                    startPaintTimer(arenaName);
                    session.setState(GameSession.GameState.PAINTING);
                    session.getPlayers().forEach(gp -> {
                        Player p = Bukkit.getPlayer(gp.getUuid());
                        if (p != null) {
                            p.sendTitle("§6§lRONDA " + session.getCurrentRound(), "§eTema: §f" + theme, 10, 60, 10);
                            GUIManager.givePalette(plugin, p);
                        }
                    });
                }
            }
        }.runTaskLater(plugin, 100L);
    }

    public void endGame(String arenaName) {
        GameSession session = sessions.get(arenaName);
        session.setState(GameSession.GameState.PODIUM);
        List<GamePlayer> top = session.getTopPlayers();
        Arena arena = plugin.getArenaManager().getArena(arenaName);

        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null) return;
            plugin.getStatsManager().addStats(gp);
            plugin.getCoinManager().addCoins(p, getCoinReward(session, gp));
            plugin.getRankManager().checkRankUp(p, gp);
            plugin.getMissionManager().checkMissions(p, session, gp);
            plugin.getBattlePassManager().addXP(p, gp.getPoints() / 10);
        });

        if (!top.isEmpty()) {
            GamePlayer winner = top.get(0);
            Bukkit.broadcastMessage(plugin.getMessageManager().get("winner-broadcast",
                    "%player%", winner.getName(), "%points%", String.valueOf(winner.getPoints())));
            Player winnerPlayer = Bukkit.getPlayer(winner.getUuid());
            if (winnerPlayer != null) {
                EffectUtil.spawnWinFireworks(winnerPlayer);
                EffectUtil.spawnWinParticles(winnerPlayer);
                winnerPlayer.sendTitle("§6§l¡GANASTE!", "§e" + winner.getPoints() + " puntos", 10, 80, 20);
                winnerPlayer.playSound(winnerPlayer.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                winner.incrementGamesWon();
            }
            plugin.getDiscordManager().announceWinner(winner.getName(), arenaName, winner.getPoints());
        }

        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null) return;
            p.sendMessage("§6§l╔══════ PODIO FINAL ══════╗");
            for (int i = 0; i < Math.min(3, top.size()); i++) {
                String medal = i == 0 ? "§6🥇" : i == 1 ? "§7🥈" : "§c🥉";
                p.sendMessage(medal + " §f" + top.get(i).getName() + " §7- §e" + top.get(i).getPoints() + " pts");
            }
            p.sendMessage("§6§l╚════════════════════════╝");
            ScoreboardUtil.updatePodiumScoreboard(plugin, p, session, arena, top);
            gp.incrementGamesPlayed();
        });

        new BukkitRunnable() {
            @Override
            public void run() { resetArena(arenaName); }
        }.runTaskLater(plugin, (long) plugin.getConfig().getInt("podium-time", 10) * 20L);
    }

    private int getCoinReward(GameSession session, GamePlayer gp) {
        List<GamePlayer> top = session.getTopPlayers();
        if (!top.isEmpty() && top.get(0).getUuid().equals(gp.getUuid()))
            return plugin.getConfig().getInt("coins-win", 100);
        if (top.size() > 1 && top.get(1).getUuid().equals(gp.getUuid()))
            return plugin.getConfig().getInt("coins-second", 60);
        if (top.size() > 2 && top.get(2).getUuid().equals(gp.getUuid()))
            return plugin.getConfig().getInt("coins-third", 30);
        return plugin.getConfig().getInt("coins-participate", 10);
    }

    private void resetArena(String arenaName) {
        GameSession session = sessions.get(arenaName);
        if (session == null) return;
        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null) {
                playerArena.remove(p.getUniqueId());
                Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena != null && arena.getLobby() != null) p.teleport(arena.getLobby());
                p.setGameMode(GameMode.SURVIVAL);
                p.getInventory().clear();
                ScoreboardUtil.removeScoreboard(p);
            }
        });
        CanvasManager.clearCanvases(plugin, session, plugin.getArenaManager().getArena(arenaName));
        sessions.remove(arenaName);
        plugin.getArenaManager().getArena(arenaName).setState(Arena.ArenaState.WAITING);
    }

    private void cancelTimer(String key) {
        BukkitRunnable t = timers.remove(key);
        if (t != null) t.cancel();
    }

    public void broadcastToSession(GameSession session, String message) {
        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null) p.sendMessage(message);
        });
    }

    public void stopAllGames() {
        new HashSet<>(sessions.keySet()).forEach(this::resetArena);
    }

    public boolean isInGame(UUID uuid) { return playerArena.containsKey(uuid); }
    public String getPlayerArena(UUID uuid) { return playerArena.get(uuid); }
    public GameSession getSession(String arenaName) { return sessions.get(arenaName); }
    public Map<String, GameSession> getSessions() { return sessions; }

    private DiscordManager getDiscordManager() { return plugin.getDiscordManager(); }
}
