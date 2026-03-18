package me.soyadrianyt001.advancedpaintbattle.gui;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.utils.ItemUtil;
import me.soyadrianyt001.advancedpaintbattle.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class ProfileGUI {

    private final AdvancedPaintBattle plugin;

    public ProfileGUI(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    public void open(Player viewer, Player target) {
        open(viewer, target.getUniqueId(), target.getName());
    }

    public void open(Player viewer, UUID targetUUID, String targetName) {
        Inventory inv = Bukkit.createInventory(null, 54, "§b§l👤 Perfil de " + targetName);

        // Relleno
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, ItemUtil.createFiller(Material.LIGHT_BLUE_STAINED_GLASS_PANE));
        }

        String rank = plugin.getRankManager().getRank(targetUUID);
        int wins = plugin.getStatsManager().getWins(targetUUID);
        int games = plugin.getStatsManager().getGamesPlayed(targetUUID);
        int points = plugin.getStatsManager().getTotalPoints(targetUUID);
        int coins = plugin.getCoinManager().getCoins(targetUUID);
        int level = plugin.getBattlePassManager().getLevel(targetUUID);
        long firstJoin = plugin.getPlayerDataManager().getFirstJoin(targetUUID);
        long lastSeen = plugin.getPlayerDataManager().getLastSeen(targetUUID);
        int achievements = plugin.getAchievementManager().getAchievements(targetUUID).size();
        double winRate = games > 0 ? Math.round((double) wins / games * 100.0) : 0;

        // Cabeza del jugador (centro)
        inv.setItem(13, ItemUtil.createSkull(targetName,
                "§f§l" + targetName,
                "§7Rango: " + rank,
                "§7Nivel BP: §d" + level,
                "",
                "§eClick para ver stats"
        ));

        // Stats
        inv.setItem(19, ItemUtil.createGlowing(Material.GOLD_BLOCK,
                "§6§lVictorias",
                "§f" + wins,
                "§7Win rate: §e" + winRate + "%"
        ));
        inv.setItem(20, ItemUtil.create(Material.PAPER,
                "§e§lPartidas Jugadas",
                "§f" + games
        ));
        inv.setItem(21, ItemUtil.create(Material.EXPERIENCE_BOTTLE,
                "§a§lPuntos Totales",
                "§f" + points
        ));
        inv.setItem(22, ItemUtil.create(Material.SUNFLOWER,
                "§6§lMonedas",
                "§f" + coins
        ));
        inv.setItem(23, ItemUtil.create(Material.NETHER_STAR,
                "§d§lLogros",
                "§f" + achievements + " desbloqueados"
        ));
        inv.setItem(24, ItemUtil.create(Material.CLOCK,
                "§7§lPrimera vez",
                firstJoin > 0 ? "§f" + TimeUtil.formatDate(firstJoin) : "§fDesconocido"
        ));
        inv.setItem(25, ItemUtil.create(Material.COMPASS,
                "§7§lUltima vez visto",
                lastSeen > 0 ? "§f" + TimeUtil.timeSince(lastSeen) : "§fAhora"
        ));

        // Trofeo de torneo si tiene
        inv.setItem(31, ItemUtil.create(Material.GOLD_INGOT,
                "§6§lTemporada " + plugin.getDataManager().getCurrentSeason(),
                "§7Puntos esta temporada: §e" + points
        ));

        // Botones
        if (!viewer.getUniqueId().equals(targetUUID)) {
            inv.setItem(47, ItemUtil.create(Material.LIME_DYE,
                    "§a§lAgregar Amigo",
                    "§7Enviar solicitud de amistad"
            ));
            inv.setItem(51, ItemUtil.create(Material.BLAZE_POWDER,
                    "§e§lRetar 1v1",
                    "§7Enviar reto de pintura"
            ));
        }

        inv.setItem(49, ItemUtil.createClose());
        viewer.openInventory(inv);
    }
}
