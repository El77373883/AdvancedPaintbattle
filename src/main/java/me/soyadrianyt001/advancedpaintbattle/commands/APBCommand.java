package me.soyadrianyt001.advancedpaintbattle.commands;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.managers.GUIManager;
import me.soyadrianyt001.advancedpaintbattle.models.Arena;
import me.soyadrianyt001.advancedpaintbattle.utils.EffectUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class APBCommand implements CommandExecutor, TabCompleter {

    private final AdvancedPaintBattle plugin;

    public APBCommand(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores.");
            return true;
        }

        if (args.length == 0) {
            GUIManager.openMainGUI(plugin, player);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "join" -> {
                if (args.length > 1) plugin.getGameManager().joinArena(player, args[1]);
                else {
                    Arena available = plugin.getArenaManager().getAvailableArena();
                    if (available != null) plugin.getGameManager().joinArena(player, available.getName());
                    else plugin.getMessageManager().send(player, "no-arenas");
                }
            }

            case "leave" -> {
                if (!plugin.getGameManager().isInGame(player.getUniqueId()))
                    plugin.getMessageManager().send(player, "not-in-game");
                else plugin.getGameManager().leaveArena(player);
            }

            case "top" -> {
                player.sendMessage("§6§l╔══════ TOP 10 GLOBAL ══════╗");
                var top = plugin.getStatsManager().getTopPlayers(10);
                for (int i = 0; i < top.size(); i++) {
                    String medal = i == 0 ? "§6#1" : i == 1 ? "§7#2" : i == 2 ? "§c#3" : "§f#" + (i + 1);
                    player.sendMessage(medal + " §f" + top.get(i).getKey() + " §7- §e" + top.get(i).getValue() + " pts");
                }
                player.sendMessage("§6§l╚══════════════════════╝");
            }

            case "stats" -> {
                String target = args.length > 1 ? args[1] : player.getName();
                Player targetPlayer = Bukkit.getPlayer(target);
                UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : player.getUniqueId();
                player.sendMessage("§b§l╔══ Stats de " + target + " ══╗");
                player.sendMessage("§7Victorias: §e" + plugin.getStatsManager().getWins(uuid));
                player.sendMessage("§7Partidas: §e" + plugin.getStatsManager().getGamesPlayed(uuid));
                player.sendMessage("§7Puntos: §e" + plugin.getStatsManager().getTotalPoints(uuid));
                player.sendMessage("§7Monedas: §e" + plugin.getCoinManager().getCoins(uuid));
                player.sendMessage("§7Rango: §e" + plugin.getRankManager().getRank(uuid));
                player.sendMessage("§b§l╚════════════════════╝");
            }

            case "spectate" -> {
                if (args.length < 2) { player.sendMessage("§cUso: /apb spectate <arena>"); return true; }
                Arena arena = plugin.getArenaManager().getArena(args[1]);
                if (arena == null) { player.sendMessage("§cArena no encontrada."); return true; }
                if (arena.getLobby() != null) player.teleport(arena.getLobby());
                player.sendMessage("§aAhora eres espectador de §e" + args[1]);
            }

            case "shop" -> GUIManager.openShopGUI(plugin, player);

            case "achievements" -> GUIManager.openAchievementsGUI(plugin, player);

            case "rank" -> player.sendMessage("§6Tu rango: §f" + plugin.getRankManager().getRank(player.getUniqueId()));

            case "missions" -> GUIManager.openMissionsGUI(plugin, player);

            case "lang" -> {
                if (args.length < 2) { player.sendMessage("§cUso: /apb lang <es/en>"); return true; }
                plugin.getConfig().set("language", args[1]);
                plugin.saveConfig();
                plugin.getMessageManager().reload();
                player.sendMessage("§aIdioma cambiado a §e" + args[1]);
            }

            case "creator" -> showCreator(player);

            case "help" -> showHelp(player);

            // ADMIN COMMANDS
            case "create" -> {
                if (!player.hasPermission("advancedpaintbattle.admin")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                if (args.length < 2) { player.sendMessage("§cUso: /apb create <nombre>"); return true; }
                plugin.getArenaManager().createArena(args[1]);
                player.sendMessage("§a¡Arena §e" + args[1] + " §acreada!");
            }

            case "delete" -> {
                if (!player.hasPermission("advancedpaintbattle.admin")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                if (args.length < 2) { player.sendMessage("§cUso: /apb delete <arena>"); return true; }
                plugin.getArenaManager().deleteArena(args[1]);
                player.sendMessage("§a¡Arena §e" + args[1] + " §aeliminada!");
            }

            case "setlobby" -> {
                if (!player.hasPermission("advancedpaintbattle.admin")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                if (args.length < 2) { player.sendMessage("§cUso: /apb setlobby <arena>"); return true; }
                Arena arena = plugin.getArenaManager().getArena(args[1]);
                if (arena == null) { player.sendMessage("§cArena no encontrada."); return true; }
                arena.setLobby(player.getLocation());
                plugin.getArenaManager().saveArenas();
                player.sendMessage("§aLobby de §e" + args[1] + " §aconfigurado!");
            }

            case "setcanvas" -> {
                if (!player.hasPermission("advancedpaintbattle.admin")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                if (args.length < 2) { player.sendMessage("§cUso: /apb setcanvas <arena>"); return true; }
                Arena arena = plugin.getArenaManager().getArena(args[1]);
                if (arena == null) { player.sendMessage("§cArena no encontrada."); return true; }
                arena.setCanvasOrigin(player.getLocation());
                plugin.getArenaManager().saveArenas();
                player.sendMessage("§aOrigen de lienzos de §e" + args[1] + " §aconfigurado!");
            }

            case "addtheme" -> {
                if (!player.hasPermission("advancedpaintbattle.admin")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                if (args.length < 3) { player.sendMessage("§cUso: /apb addtheme <categoria> <tema>"); return true; }
                List<String> themes = plugin.getConfig().getStringList("themes." + args[1]);
                themes.add(args[2]);
                plugin.getConfig().set("themes." + args[1], themes);
                plugin.saveConfig();
                player.sendMessage("§aTema §e" + args[2] + " §aañadido a §e" + args[1]);
            }

            case "reload" -> {
                if (!player.hasPermission("advancedpaintbattle.admin")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                plugin.reloadConfig();
                plugin.getMessageManager().reload();
                player.sendMessage("§a¡Configuracion recargada!");
            }

            case "give" -> {
                if (!player.hasPermission("advancedpaintbattle.admin")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                if (args.length < 3) { player.sendMessage("§cUso: /apb give <jugador> <monedas>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { player.sendMessage("§cJugador no encontrado."); return true; }
                int amount = Integer.parseInt(args[2]);
                plugin.getCoinManager().addCoins(target, amount);
                player.sendMessage("§aDiste §e" + amount + " §amonedas a §e" + target.getName());
            }

            case "forcestart" -> {
                if (!player.hasPermission("advancedpaintbattle.admin")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                if (args.length < 2) { player.sendMessage("§cUso: /apb forcestart <arena>"); return true; }
                plugin.getGameManager().startGame(args[1]);
                player.sendMessage("§aPartida forzada en §e" + args[1]);
            }

            case "setwarp" -> {
                if (!player.hasPermission("advancedpaintbattle.admin")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                player.sendMessage("§aWarp configurado en tu posicion actual.");
            }

            default -> showHelp(player);
        }
        return true;
    }

    private void showCreator(Player player) {
        EffectUtil.spawnCreatorParticles(player);
        player.playSound(player.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

        String[] colors = {"§c", "§6", "§e", "§a", "§b", "§9", "§d"};
        String name = "soyadrianyt001";
        StringBuilder rainbow = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            rainbow.append(colors[i % colors.length]).append(name.charAt(i));
        }

        player.sendMessage("§6§l╔══════════════════════════════════════╗");
        player.sendMessage("§6§l║                                      §6§l║");
        player.sendMessage("§6§l║    §e§l✦ §6§lAdvanced§e§lPaintBattle §e§l✦       §6§l║");
        player.sendMessage("§6§l║                                      §6§l║");
        player.sendMessage("§6§l║   §7El plugin de pintura mas avanzado  §6§l║");
        player.sendMessage("§6§l║          §7de Minecraft                §6§l║");
        player.sendMessage("§6§l║                                      §6§l║");
        player.sendMessage("§6§l║      §7Creado con §c❤ §7por:             §6§l║");
        player.sendMessage("§6§l║                                      §6§l║");
        player.sendMessage("§6§l║       §l" + rainbow + "          §6§l║");
        player.sendMessage("§6§l║                                      §6§l║");
        player.sendMessage("§6§l║    §7Version: §av1.0.0                  §6§l║");
        player.sendMessage("§6§l║    §7Plugin: §eAdvancedPaintBattle      §6§l║");
        player.sendMessage("§6§l║                                      §6§l║");
        player.sendMessage("§6§l╚══════════════════════════════════════╝");
    }

    private void showHelp(Player player) {
        player.sendMessage("§6§l╔══════ AdvancedPaintBattle §7v1.0.0 §6§l══════╗");
        player.sendMessage("§e/apb join §8[arena] §7- Unirse a una partida");
        player.sendMessage("§e/apb leave §7- Salir de la partida");
        player.sendMessage("§e/apb top §7- Top 10 global");
        player.sendMessage("§e/apb stats §8[jugador] §7- Ver estadisticas");
        player.sendMessage("§e/apb spectate §8<arena> §7- Modo espectador");
        player.sendMessage("§e/apb shop §7- Tienda de pinceles");
        player.sendMessage("§e/apb achievements §7- Ver logros");
        player.sendMessage("§e/apb rank §7- Ver tu rango");
        player.sendMessage("§e/apb missions §7- Misiones diarias");
        player.sendMessage("§e/apb lang §8<es/en> §7- Cambiar idioma");
        player.sendMessage("§e/apb creator §7- Info del creador");
        if (player.hasPermission("advancedpaintbattle.admin")) {
            player.sendMessage("§c§l-- ADMIN --");
            player.sendMessage("§c/apb create/delete/setlobby/setcanvas/addtheme/reload/give/forcestart/setwarp");
        }
        player.sendMessage("§6§l╚══════════════════════════════════════╝");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("join", "leave", "top", "stats", "spectate", "shop",
                    "achievements", "rank", "missions", "lang", "creator", "help",
                    "create", "delete", "setlobby", "setcanvas", "addtheme", "reload", "give", "forcestart", "setwarp");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("setlobby")
                || args[0].equalsIgnoreCase("setcanvas") || args[0].equalsIgnoreCase("delete")
                || args[0].equalsIgnoreCase("forcestart") || args[0].equalsIgnoreCase("spectate"))) {
            return new ArrayList<>(plugin.getArenaManager().getArenas().keySet());
        }
        return new ArrayList<>();
    }
}
