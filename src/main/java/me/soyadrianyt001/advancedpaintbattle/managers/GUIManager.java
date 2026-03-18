package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GamePlayer;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class GUIManager {

    public static void givePalette(AdvancedPaintBattle plugin, Player player) {
        player.getInventory().clear();
        List<String> palette = plugin.getConfig().getStringList("palette");
        for (int i = 0; i < Math.min(palette.size(), 9); i++) {
            Material mat = Material.getMaterial(palette.get(i));
            if (mat != null) player.getInventory().setItem(i, new ItemStack(mat));
        }
        player.getInventory().setItem(9, createItem(Material.STICK, "§e§lPincel Normal", "§7Click para pintar"));
        player.getInventory().setItem(10, createItem(Material.BLAZE_ROD, "§6§lPincel 3x3", "§7Pinta 3x3 bloques"));
        player.getInventory().setItem(11, createItem(Material.BUCKET, "§b§lBalde de Relleno", "§7Rellena un area"));
        player.getInventory().setItem(12, createItem(Material.BOW, "§d§lPincel Arcoiris", "§7Colores magicos"));
        player.getInventory().setItem(13, createItem(Material.ENDER_PEARL, "§5§lPincel Invisible", "§7Nadie ve tu dibujo"));
        player.getInventory().setItem(14, createItem(Material.ARROW, "§a§lLinea Recta", "§7Pinta en linea recta"));
        player.getInventory().setItem(15, createItem(Material.COMPASS, "§f§lEspejo", "§7Refleja tu pintura"));
        player.getInventory().setItem(17, createItem(Material.BARRIER, "§c§lBorrador", "§7Borra bloques"));
        player.getInventory().setItem(18, createItem(Material.BOOK, "§e§lPaleta Completa", "§7Abre la paleta de colores"));
        player.getInventory().setItem(26, createItem(Material.NETHER_STAR, "§6§lDeshacer §8(Z)", "§7Deshace el ultimo bloque"));
    }

    public static void openPaletteGUI(AdvancedPaintBattle plugin, Player player) {
        List<String> palette = plugin.getConfig().getStringList("palette");
        int size = (int) Math.ceil(palette.size() / 9.0) * 9;
        if (size == 0) size = 9;
        Inventory inv = Bukkit.createInventory(null, Math.min(54, size), "§6§lPaleta de Colores");
        for (int i = 0; i < Math.min(palette.size(), 54); i++) {
            Material mat = Material.getMaterial(palette.get(i));
            if (mat != null) {
                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§f" + formatName(palette.get(i)));
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
        }
        player.openInventory(inv);
    }

    public static void openVotingGUI(AdvancedPaintBattle plugin, Player player, GameSession session) {
        int size = Math.min(54, ((session.getPlayers().size() / 9) + 1) * 9);
        Inventory inv = Bukkit.createInventory(null, size, "§6§l¡Vota por el mejor!");
        for (int i = 0; i < session.getPlayers().size(); i++) {
            GamePlayer gp = session.getPlayers().get(i);
            if (gp.getUuid().equals(player.getUniqueId())) continue;
            Player target = Bukkit.getPlayer(gp.getUuid());
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (target != null) meta.setOwningPlayer(target);
            meta.setDisplayName("§e§lVotar por §b" + gp.getName());
            meta.setLore(Arrays.asList(
                    "§7Votos: §e" + gp.getVotes(),
                    "§7Puntos: §a" + gp.getPoints(),
                    "",
                    "§aClick para votar!"
            ));
            skull.setItemMeta(meta);
            inv.setItem(i, skull);
        }
        player.openInventory(inv);
    }

    public static void openMainGUI(AdvancedPaintBattle plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6§lAdvancedPaintBattle");
        inv.setItem(11, createItem(Material.LIME_WOOL, "§a§lUnirse a Partida", "§7Entra a una arena disponible"));
        inv.setItem(13, createItem(Material.DIAMOND_SWORD, "§b§lEstadisticas", "§7Ver tus stats"));
        inv.setItem(15, createItem(Material.GOLD_INGOT, "§6§lTienda", "§7Comprar pinceles y cosmeticos"));
        inv.setItem(29, createItem(Material.BOOK, "§d§lLogros", "§7Ver tus logros"));
        inv.setItem(31, createItem(Material.NETHER_STAR, "§e§lTop Global", "§7Ver el ranking mundial"));
        inv.setItem(33, createItem(Material.SHIELD, "§c§lTorneos", "§7Ver torneos activos"));
        inv.setItem(49, createItem(Material.BARRIER, "§c§lCerrar", "§7Cierra el menu"));
        player.openInventory(inv);
    }

    public static void openStatsGUI(AdvancedPaintBattle plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§b§lTus Estadisticas");
        int[] wins = {plugin.getStatsManager().getWins(player.getUniqueId())};
        int[] played = {plugin.getStatsManager().getGamesPlayed(player.getUniqueId())};
        int[] points = {plugin.getStatsManager().getTotalPoints(player.getUniqueId())};
        int[] coins = {plugin.getCoinManager().getCoins(player.getUniqueId())};
        String rank = plugin.getRankManager().getRank(player.getUniqueId());

        inv.setItem(10, createItem(Material.GOLD_BLOCK, "§6Victorias", "§f" + wins[0]));
        inv.setItem(12, createItem(Material.PAPER, "§ePartidas Jugadas", "§f" + played[0]));
        inv.setItem(14, createItem(Material.EXPERIENCE_BOTTLE, "§aPuntos Totales", "§f" + points[0]));
        inv.setItem(16, createItem(Material.SUNFLOWER, "§6Monedas", "§f" + coins[0]));
        inv.setItem(4, createItem(Material.NETHER_STAR, "§dRango", "§f" + rank));
        player.openInventory(inv);
    }

    public static void openShopGUI(AdvancedPaintBattle plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6§lTienda de Pinceles");
        inv.setItem(10, createShopItem(Material.BLAZE_ROD, "§6Pincel Gigante 5x5", "§7500 monedas", 500));
        inv.setItem(12, createShopItem(Material.GLOWSTONE_DUST, "§eParticulas al Pintar", "§7300 monedas", 300));
        inv.setItem(14, createShopItem(Material.NETHER_STAR, "§dPincel de Estrellas", "§71000 monedas", 1000));
        inv.setItem(16, createShopItem(Material.ENDER_EYE, "§5Pincel Magico", "§7750 monedas", 750));
        player.openInventory(inv);
    }

    public static void openMissionsGUI(AdvancedPaintBattle plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§d§lMisiones Diarias");
        List<String> missions = plugin.getMissionManager().getMissions(player.getUniqueId());
        for (int i = 0; i < Math.min(missions.size(), 9); i++) {
            inv.setItem(i + 9, createItem(Material.BOOK, "§e" + missions.get(i), "§7Completa esta mision"));
        }
        player.openInventory(inv);
    }

    public static void openAchievementsGUI(AdvancedPaintBattle plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§d§lLogros");
        List<String> achievements = plugin.getAchievementManager().getAchievements(player.getUniqueId());
        for (int i = 0; i < Math.min(achievements.size(), 54); i++) {
            inv.setItem(i, createItem(Material.NETHER_STAR, "§e" + achievements.get(i), "§7Logro desbloqueado"));
        }
        player.openInventory(inv);
    }

    public static ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createShopItem(Material mat, String name, String price, int cost) {
        return createItem(mat, name, price, "§aClick para comprar");
    }

    private static String formatName(String raw) {
        return raw.replace("_", " ").toLowerCase();
    }
}
