package me.soyadrianyt001.advancedpaintbattle.utils;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatUtil implements Listener {

    private final AdvancedPaintBattle plugin;

    public ChatUtil(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;

        String arenaName = plugin.getGameManager().getPlayerArena(player.getUniqueId());
        GameSession session = plugin.getGameManager().getSession(arenaName);
        if (session == null) return;

        e.setCancelled(true);

        String rank = plugin.getRankManager().getRank(player.getUniqueId());
        String msg = e.getMessage();
        String formatted = "§8[§6Arena§8] " + rank + " §f" + player.getName() + "§8: §7" + msg;

        // Solo jugadores de la misma arena ven el chat
        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null) p.sendMessage(formatted);
        });
        session.getSpectators().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null) p.sendMessage("§8[§7Espectador§8] " + formatted);
        });

        // Log en consola
        Bukkit.getConsoleSender().sendMessage(formatted);
    }

    public static void sendArenaMessage(AdvancedPaintBattle plugin, String arenaName, String message) {
        GameSession session = plugin.getGameManager().getSession(arenaName);
        if (session == null) return;
        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null) p.sendMessage(message);
        });
    }

    public static void broadcastTitle(AdvancedPaintBattle plugin, String arenaName,
                                       String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        GameSession session = plugin.getGameManager().getSession(arenaName);
        if (session == null) return;
        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null) p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        });
    }

    public static void broadcastActionBar(AdvancedPaintBattle plugin, String arenaName, String message) {
        GameSession session = plugin.getGameManager().getSession(arenaName);
        if (session == null) return;
        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null) p.sendActionBar(net.kyori.adventure.text.Component.text(
                    message.replace("&", "§")));
        });
    }

    public static String colorize(String text) {
        return text.replace("&", "§");
    }

    public static String stripColor(String text) {
        return text.replaceAll("§[0-9a-fk-or]", "");
    }
}
