package me.soyadrianyt001.advancedpaintbattle.listeners;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.managers.GUIManager;
import me.soyadrianyt001.advancedpaintbattle.models.GamePlayer;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class GUIListener implements Listener {

    private final AdvancedPaintBattle plugin;

    public GUIListener(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        String title = e.getView().getTitle();
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().isAir()) return;

        // Menu principal
        if (title.equals("§6§lAdvancedPaintBattle")) {
            e.setCancelled(true);
            handleMainGUI(player, item);
            return;
        }

        // Paleta de colores
        if (title.equals("§6§lPaleta de Colores")) {
            e.setCancelled(true);
            String arenaName = plugin.getGameManager().getPlayerArena(player.getUniqueId());
            if (arenaName == null) return;
            player.getInventory().setItem(0, new ItemStack(item.getType()));
            player.closeInventory();
            return;
        }

        // Votacion
        if (title.equals("§6§l¡Vota por el mejor!")) {
            e.setCancelled(true);
            if (item.getType() != Material.PLAYER_HEAD) return;
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta == null || meta.getOwningPlayer() == null) return;
            UUID targetUuid = meta.getOwningPlayer().getUniqueId();
            String arenaName = plugin.getGameManager().getPlayerArena(player.getUniqueId());
            if (arenaName != null) plugin.getGameManager().vote(player, targetUuid, arenaName);
            player.closeInventory();
            return;
        }

        // Tienda
        if (title.equals("§6§lTienda de Pinceles")) {
            e.setCancelled(true);
            handleShopGUI(player, item);
            return;
        }

        // Galeria
        if (title.equals("§6§l🖼 Galería de Dibujos")) {
            e.setCancelled(true);
            if (item.getType() == Material.PLAYER_HEAD) {
                player.sendMessage("§a§l✔ §eDiste like a este dibujo!");
            }
            if (item.getType() == Material.BARRIER) player.closeInventory();
            return;
        }

        // Amigos
        if (title.equals("§b§l👥 Amigos")) {
            e.setCancelled(true);
            if (e.isRightClick() && item.getType() == Material.PLAYER_HEAD) {
                player.sendMessage("§c§l✗ §eAmigo eliminado.");
            }
            if (item.getType() == Material.LIME_DYE) {
                player.closeInventory();
                player.sendMessage("§7Usa §e/apb friend add <jugador> §7para agregar amigos.");
            }
            if (item.getType() == Material.BARRIER) player.closeInventory();
            return;
        }

        // Torneo
        if (title.equals("§6§l🏆 Torneo")) {
            e.setCancelled(true);
            if (item.getType() == Material.LIME_WOOL) {
                player.closeInventory();
                player.performCommand("apb join");
            }
            if (item.getType() == Material.COMMAND_BLOCK &&
                    player.hasPermission("advancedpaintbattle.admin")) {
                plugin.getTournamentManager().startTournament();
                plugin.getAdminLogger().log(player, "Inicio torneo manualmente");
            }
            if (item.getType() == Material.REDSTONE_BLOCK &&
                    player.hasPermission("advancedpaintbattle.admin")) {
                plugin.getTournamentManager().endTournament();
                plugin.getAdminLogger().log(player, "Termino torneo manualmente");
            }
            if (item.getType() == Material.BARRIER) player.closeInventory();
            return;
        }

        // Pase de batalla
        if (title.equals("§d§l🎫 Pase de Batalla")) {
            e.setCancelled(true);
            if (item.getType() == Material.BARRIER) player.closeInventory();
            return;
        }

        // Logros
        if (title.equals("§d§lLogros")) {
            e.setCancelled(true);
            if (item.getType() == Material.BARRIER) player.closeInventory();
            return;
        }

        // Misiones
        if (title.equals("§d§lMisiones Diarias")) {
            e.setCancelled(true);
            if (item.getType() == Material.BARRIER) player.closeInventory();
            return;
        }

        // Perfil
        if (title.startsWith("§b§l👤 Perfil de")) {
            e.setCancelled(true);
            if (item.getType() == Material.LIME_DYE) {
                String targetName = title.replace("§b§l👤 Perfil de ", "");
                Player target = org.bukkit.Bukkit.getPlayer(targetName);
                if (target != null) {
                    plugin.getFriendManager().sendRequest(player, target);
                    player.closeInventory();
                }
            }
            if (item.getType() == Material.BLAZE_POWDER) {
                player.closeInventory();
                player.sendMessage("§e§l⚔ §7Reto 1v1 enviado!");
            }
            if (item.getType() == Material.BARRIER) player.closeInventory();
            return;
        }

        // Seleccion de modo
        if (title.equals("§6§lSeleccionar Modo")) {
            e.setCancelled(true);
            handleGameModeGUI(player, item);
            return;
        }

        // Stats
        if (title.startsWith("§b§l👤 Perfil")) {
            e.setCancelled(true);
            if (item.getType() == Material.BARRIER) player.closeInventory();
        }
    }

    private void handleMainGUI(Player player, ItemStack item) {
        player.closeInventory();
        switch (item.getType()) {
            case LIME_WOOL -> player.performCommand("apb join");
            case DIAMOND_SWORD -> GUIManager.openStatsGUI(plugin, player);
            case GOLD_INGOT -> GUIManager.openShopGUI(plugin, player);
            case SHIELD -> GUIManager.openTournamentGUI(plugin, player);
            case BOOK -> GUIManager.openAchievementsGUI(plugin, player);
            case NETHER_STAR -> player.performCommand("apb top");
            case PAINTING -> GUIManager.openGalleryGUI(plugin, player);
            case HEART_OF_THE_SEA -> GUIManager.openFriendGUI(plugin, player);
            case TOTEM_OF_UNDYING -> GUIManager.openBattlePassGUI(plugin, player);
            case CLOCK -> GUIManager.openMissionsGUI(plugin, player);
            case BARRIER -> {}
            default -> {}
        }
    }

    private void handleShopGUI(Player player, ItemStack item) {
        int cost = 0;
        String brushName = "";
        switch (item.getType()) {
            case BLAZE_ROD -> { cost = 500; brushName = "Pincel Gigante 5x5"; }
            case GLOWSTONE_DUST -> { cost = 300; brushName = "Particulas al Pintar"; }
            case NETHER_STAR -> { cost = 1000; brushName = "Pincel de Estrellas"; }
            case ENDER_EYE -> { cost = 750; brushName = "Pincel Magico"; }
            case FIRE_CHARGE -> { cost = 600; brushName = "Pincel de Fuego"; }
            case SNOWBALL -> { cost = 400; brushName = "Pincel de Hielo"; }
            case SLIME_BALL -> { cost = 250; brushName = "Pincel Slime"; }
            case BARRIER -> { player.closeInventory(); return; }
            default -> { return; }
        }
        if (cost > 0) {
            if (plugin.getCoinManager().removeCoins(player, cost)) {
                player.sendMessage("§a§l✔ §eCompraste: §f" + brushName + "§e!");
                player.sendMessage("§7Usa el pincel desde tu inventario en la partida.");
                plugin.getAdminLogger().log("Compra: " + player.getName() + " compro " + brushName);
            } else {
                plugin.getMessageManager().send(player, "no-coins");
            }
        }
    }

    private void handleGameModeGUI(Player player, ItemStack item) {
        player.closeInventory();
        String mode = switch (item.getType()) {
            case PAINTING -> "NORMAL";
            case ENDER_EYE -> "BLIND";
            case LIGHTNING_ROD -> "CHAOS";
            case FIREWORK_ROCKET -> "RAPIDFIRE";
            case LIME_WOOL -> "COLLAB";
            case IRON_SWORD -> "BATTLE";
            case SHIELD -> "TEAM";
            case BARRIER -> null;
            default -> null;
        };
        if (mode != null) {
            player.sendMessage("§a§l✔ §eModo §f" + mode + " §eseleccionado!");
            player.performCommand("apb join");
        }
    }
}
