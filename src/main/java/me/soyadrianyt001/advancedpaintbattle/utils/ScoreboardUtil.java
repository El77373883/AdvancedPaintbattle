package me.soyadrianyt001.advancedpaintbattle.utils;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.Arena;
import me.soyadrianyt001.advancedpaintbattle.models.GamePlayer;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;

public class ScoreboardUtil {

    public static void updateLobbyScoreboard(AdvancedPaintBattle plugin, Player player, GameSession session, Arena arena) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("apb", Criteria.DUMMY,
                colorize("&6&l✦ &e&lAdvanced&6Paint &6&l✦"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int line = 15;
        setLine(obj, board, line--, "&6&m                  ");
        setLine(obj, board, line--, "&eModo: &fNormal");
        setLine(obj, board, line--, "&eArena: &f" + arena.getName());
        setLine(obj, board, line--, " ");
        setLine(obj, board, line--, "&eJugadores:");
        setLine(obj, board, line--, "&f " + session.getPlayers().size() + "&7/&f" + arena.getMaxPlayers());
        setLine(obj, board, line--, "  ");
        setLine(obj, board, line--, "&eEsperando...");
        setLine(obj, board, line--, "&7⏳ " + session.getTimeLeft() + "s");
        setLine(obj, board, line--, "   ");
        setLine(obj, board, line--, "&6Rango: &f" + plugin.getRankManager().getRank(player.getUniqueId()));
        setLine(obj, board, line--, "&6Monedas: &e" + plugin.getCoinManager().getCoins(player.getUniqueId()));
        setLine(obj, board, line--, "    ");
        setLine(obj, board, line--, "&6&m                  ");
        setLine(obj, board, line, "&eby &bsoyadrianyt001");

        player.setScoreboard(board);
    }

    public static void updateGameScoreboard(AdvancedPaintBattle plugin, Player player, GameSession session, Arena arena) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("apb", Criteria.DUMMY,
                colorize("&6&l✦ &e&lAdvanced&6Paint &6&l✦"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<GamePlayer> top = session.getTopPlayers();
        GamePlayer gp = session.getGamePlayer(player.getUniqueId());
        int pts = gp != null ? gp.getPoints() : 0;

        int line = 15;
        setLine(obj, board, line--, "&6&m                  ");
        setLine(obj, board, line--, "&eTema: &f" + session.getCurrentTheme());
        setLine(obj, board, line--, "&eRonda: &f" + session.getCurrentRound() + "/" + session.getMaxRounds());
        setLine(obj, board, line--, " ");
        setLine(obj, board, line--, "&c⏱ &f" + session.getTimeLeft() + "s");
        setLine(obj, board, line--, "&eFase: &aPintando");
        setLine(obj, board, line--, "  ");
        setLine(obj, board, line--, "&eTus puntos: &a" + pts);
        setLine(obj, board, line--, "   ");
        setLine(obj, board, line--, "&6Top ronda:");
        for (int i = 0; i < Math.min(3, top.size()); i++) {
            String medal = i == 0 ? "&6#1" : i == 1 ? "&7#2" : "&c#3";
            setLine(obj, board, line--, medal + " &f" + top.get(i).getName() + " &7" + top.get(i).getPoints());
        }
        setLine(obj, board, line--, "&6&m                  ");
        setLine(obj, board, line, "&eby &bsoyadrianyt001");

        player.setScoreboard(board);
    }

    public static void updateVoteScoreboard(AdvancedPaintBattle plugin, Player player, GameSession session, Arena arena) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("apb", Criteria.DUMMY,
                colorize("&6&l✦ &e&lAdvanced&6Paint &6&l✦"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int line = 15;
        setLine(obj, board, line--, "&6&m                  ");
        setLine(obj, board, line--, "&eFase: &6Votando");
        setLine(obj, board, line--, "&eTema: &f" + session.getCurrentTheme());
        setLine(obj, board, line--, " ");
        setLine(obj, board, line--, "&c⏱ Votar en: &f" + session.getTimeLeft() + "s");
        setLine(obj, board, line--, "  ");
        setLine(obj, board, line--, "&eAbre el inventario");
        setLine(obj, board, line--, "&epara votar!");
        setLine(obj, board, line--, "   ");
        setLine(obj, board, line--, "&6&m                  ");
        setLine(obj, board, line, "&eby &bsoyadrianyt001");

        player.setScoreboard(board);
    }

    public static void updatePodiumScoreboard(AdvancedPaintBattle plugin, Player player, GameSession session, Arena arena, List<GamePlayer> top) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("apb", Criteria.DUMMY,
                colorize("&6&l✦ &e&lAdvanced&6Paint &6&l✦"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int line = 15;
        setLine(obj, board, line--, "&6&m                  ");
        setLine(obj, board, line--, "&6&l★ PODIO FINAL ★");
        setLine(obj, board, line--, " ");
        if (top.size() > 0) setLine(obj, board, line--, "&6🥇 &f" + top.get(0).getName() + " &7" + top.get(0).getPoints() + "pts");
        if (top.size() > 1) setLine(obj, board, line--, "&7🥈 &f" + top.get(1).getName() + " &7" + top.get(1).getPoints() + "pts");
        if (top.size() > 2) setLine(obj, board, line--, "&c🥉 &f" + top.get(2).getName() + " &7" + top.get(2).getPoints() + "pts");
        setLine(obj, board, line--, "  ");
        setLine(obj, board, line--, "&6&m                  ");
        setLine(obj, board, line, "&eby &bsoyadrianyt001");

        player.setScoreboard(board);
    }

    public static void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    private static void setLine(Objective obj, Scoreboard board, int line, String text) {
        String colored = colorize(text);
        Team team = board.registerNewTeam("line_" + line);
        String entry = ChatColor.values()[line % ChatColor.values().length].toString();
        team.addEntry(entry);
        team.setPrefix(colored);
        obj.getScore(entry).setScore(line);
    }

    private static String colorize(String text) {
        return text.replace("&", "§");
    }
}
