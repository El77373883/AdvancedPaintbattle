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

        if (title.contains("AdvancedPaintBattle") && !title.contains("Vota")) {
            e.setCancelled(true);
            handleMainGUI(player, item, title);
        }

        if (title.contains("Paleta de Colores")) {
            e.setCancelled(true);
            String arenaName = plugin.getGameManager().getPlayerArena(player.getUniqueId());
            if (arenaName == null) return;
            GameSession session = plugin.getGameManager().getSession(arenaName);
            if (session == null) return;
            GamePlayer gp = session.getGamePlayer(player.getUniqueId());
            if (gp != null) player.getInventory().setItem(0, new ItemStack(item.getType()));
            player.closeInventory();
        }

        if (title.contains("¡Vota por el mejor!")) {
            e.setCancelled(true);
            if (item.getType() != Material.PLAYER_HEAD) return;
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta == null || meta.getOwningPlayer() == null) return;
            UUID targetUuid = meta.getOwningPlayer().getUniqueId();
            String arenaName = plugin.getGameManager().getPlayerArena(player.getUniqueId());
            if (arenaName != null) plugin.getGameManager().vote(player, targetUuid, arenaName);
            player.closeInventory();
        }

        if (title.contains("Tienda de Pinceles")) {
            e.setCancelled(true);
            handleShop(player, item);
        }
    }

    private void handleMainGUI(Player player, ItemStack item, String title) {
        if (item.getType() == Material.LIME_WOOL) {
            player.closeInventory();
            player.performCommand("apb join");
        } else if (item.getType() == Material.DIAMOND_SWORD) {
            player.closeInventory();
            GUIManager.openStatsGUI(plugin, player);
        } else if (item.getType() == Material.GOLD_INGOT) {
            player.closeInventory();
            GUIManager.openShopGUI(plugin, player);
        } else if (item.getType() == Material.BOOK) {
            player.closeInventory();
            GUIManager.openAchievementsGUI(plugin, player);
        } else if (item.getType() == Material.NETHER_STAR) {
            player.closeInventory();
            player.performCommand("apb top");
        } else if (item.getType() == Material.BARRIER) {
            player.closeInventory();
        }
    }

    private void handleShop(Player player, ItemStack item) {
        int cost = 0;
        String brush = "";
        if (item.getType() == Material.BLAZE_ROD) { cost = 500; brush = "BIG_5x5"; }
        else if (item.getType() == Material.GLOWSTONE_DUST) { cost = 300; brush = "PARTICLES"; }
        else if (item.getType() == Material.NETHER_STAR) { cost = 1000; brush = "STAR"; }
        else if (item.getType() == Material.ENDER_EYE) { cost = 750; brush = "MAGIC"; }
        if (!brush.isEmpty() && plugin.getCoinManager().removeCoins(player, cost)) {
            player.sendMessage("§a¡Compraste el pincel §e" + brush + "§a!");
        }
    }
}
