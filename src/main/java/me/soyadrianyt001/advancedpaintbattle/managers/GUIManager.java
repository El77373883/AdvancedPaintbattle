package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.gui.*;
import me.soyadrianyt001.advancedpaintbattle.models.GamePlayer;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import me.soyadrianyt001.advancedpaintbattle.utils.ItemUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
        player.getInventory().setItem(9, ItemUtil.create(Material.STICK, "§e§lPincel Normal", "§7Click para pintar"));
        player.getInventory().setItem(10, ItemUtil.create(Material.BLAZE_ROD, "§6§lPincel 3x3", "§7Pinta 3x3 bloques"));
        player.getInventory().setItem(11, ItemUtil.create(Material.BUCKET, "§b§lBalde de Relleno", "§7Rellena un area"));
        player.getInventory().setItem(12, ItemUtil.create(Material.BOW, "§d§lPincel Arcoiris", "§7Colores magicos"));
        player.getInventory().setItem(13, ItemUtil.create(Material.ENDER_PEARL, "§5§lPincel Invisible", "§7Nadie ve tu dibujo"));
        player.getInventory().setItem(14, ItemUtil.create(Material.ARROW, "§a§lLinea Recta", "§7Pinta en linea recta"));
        player.getInventory().setItem(15, ItemUtil.create(Material.COMPASS, "§f§lEspejo", "§7Refleja tu pintura"));
        player.getInventory().setItem(17, ItemUtil.create(Material.BARRIER, "§c§lBorrador", "§7Borra bloques"));
        player.getInventory().setItem(18, ItemUtil.create(Material.BOOK, "§e§lPaleta Completa", "§7Abre todos los colores"));
        player.getInventory().setItem(26, ItemUtil.create(Material.NETHER_STAR, "§6§lDeshacer §8(F)", "§7Deshace el ultimo bloque"));
    }

    public static void openPaletteGUI(AdvancedPaintBattle plugin, Player player) {
        List<String> palette = plugin.getConfig().getStringList("palette");
        int size = (int) Math.ceil(palette.size() / 9.0) * 9;
        if (size == 0 || size > 54) size = 54;
        Inventory inv = Bukkit.createInventory(null, size, "§6§lPaleta de Colores");
        for (int i = 0; i < Math.min(palette.size(), 54); i++) {
            Material mat = Material.getMaterial(palette.get(i));
            if (mat != null) {
                inv.setItem(i, ItemUtil.create(mat, "§f" + palette.get(i).replace("_", " ").toLowerCase(),
                        "§7Click para seleccionar"));
            }
        }
        player.openInventory(inv);
    }

    public static void openVotingGUI(AdvancedPaintBattle plugin, Player player, GameSession session) {
        int size = Math.min(54, ((session.getPlayers().size() / 9) + 1) * 9);
        if (size < 9) size = 9;
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
                    "§7Votos recibidos: §e" + gp.getVotes(),
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

        // Relleno
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, ItemUtil.createFiller(Material.BLACK_STAINED_GLASS_PANE));
        }

        // Info del jugador
        String rank = plugin.getRankManager().getRank(player.getUniqueId());
        int coins = plugin.getCoinManager().getCoins(player.getUniqueId());
        inv.setItem(4, ItemUtil.createSkull(player.getName(),
                "§f§l" + player.getName(),
                "§7Rango: " + rank,
                "§6Monedas: §e" + coins
        ));

        inv.setItem(19, ItemUtil.createGlowing(Material.LIME_WOOL, "§a§lUnirse a Partida", "§7Entra a una arena disponible"));
        inv.setItem(21, ItemUtil.create(Material.DIAMOND_SWORD, "§b§lMis Estadisticas", "§7Ver tus stats completos"));
        inv.setItem(23, ItemUtil.create(Material.GOLD_INGOT, "§6§lTienda", "§7Comprar pinceles y cosmeticos"));
        inv.setItem(25, ItemUtil.create(Material.SHIELD, "§c§lTorneos", "§7Ver torneos activos"));
        inv.setItem(28, ItemUtil.create(Material.BOOK, "§d§lLogros", "§7Ver tus logros"));
        inv.setItem(30, ItemUtil.create(Material.NETHER_STAR, "§e§lTop Global", "§7Ver el ranking mundial"));
        inv.setItem(32, ItemUtil.create(Material.PAINTING, "§6§lGalería", "§7Ver los mejores dibujos"));
        inv.setItem(34, ItemUtil.create(Material.HEART_OF_THE_SEA, "§b§lAmigos", "§7Ver tus amigos"));
        inv.setItem(40, ItemUtil.create(Material.TOTEM_OF_UNDYING, "§d§lPase de Batalla", "§7Ver tu progreso"));
        inv.setItem(42, ItemUtil.create(Material.CLOCK, "§7§lMisiones", "§7Misiones diarias"));
        inv.setItem(49, ItemUtil.createClose());

        player.openInventory(inv);
    }

    public static void openStatsGUI(AdvancedPaintBattle plugin, Player player) {
        new ProfileGUI(plugin).open(player, player);
    }

    public static void openShopGUI(AdvancedPaintBattle plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6§lTienda de Pinceles");
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, ItemUtil.createFiller(Material.ORANGE_STAINED_GLASS_PANE));
        }
        int coins = plugin.getCoinManager().getCoins(player.getUniqueId());
        inv.setItem(4, ItemUtil.createInfo("§6§lTienda", "§7Tus monedas: §e" + coins));
        inv.setItem(10, ItemUtil.createGlowing(Material.BLAZE_ROD, "§6§lPincel Gigante 5x5", "§7Precio: §e500 monedas", "§7Pinta en area 5x5"));
        inv.setItem(12, ItemUtil.createGlowing(Material.GLOWSTONE_DUST, "§e§lParticulas al Pintar", "§7Precio: §e300 monedas", "§7Efectos de particulas"));
        inv.setItem(14, ItemUtil.createGlowing(Material.NETHER_STAR, "§d§lPincel de Estrellas", "§7Precio: §e1000 monedas", "§7Pincel especial"));
        inv.setItem(16, ItemUtil.createGlowing(Material.ENDER_EYE, "§5§lPincel Magico", "§7Precio: §e750 monedas", "§7Efectos magicos"));
        inv.setItem(19, ItemUtil.createGlowing(Material.FIRE_CHARGE, "§c§lPincel de Fuego", "§7Precio: §e600 monedas", "§7Colores de fuego"));
        inv.setItem(21, ItemUtil.createGlowing(Material.SNOWBALL, "§b§lPincel de Hielo", "§7Precio: §e400 monedas", "§7Colores de hielo"));
        inv.setItem(23, ItemUtil.createGlowing(Material.SLIME_BALL, "§a§lPincel Slime", "§7Precio: §e250 monedas", "§7Colores verdes"));
        inv.setItem(49, ItemUtil.createClose());
        player.openInventory(inv);
    }

    public static void openMissionsGUI(AdvancedPaintBattle plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§d§lMisiones Diarias");
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, ItemUtil.createFiller(Material.MAGENTA_STAINED_GLASS_PANE));
        }
        inv.setItem(4, ItemUtil.createInfo("§d§lMisiones Diarias", "§7Completa misiones para ganar monedas"));
        List<String> missions = plugin.getMissionManager().getMissions(player.getUniqueId());
        int[] slots = {10, 13, 16};
        for (int i = 0; i < Math.min(missions.size(), slots.length); i++) {
            inv.setItem(slots[i], ItemUtil.createGlowing(Material.BOOK,
                    "§e§lMision " + (i + 1),
                    "§f" + missions.get(i),
                    "",
                    "§7+50 monedas al completar"
            ));
        }
        inv.setItem(22, ItemUtil.createClose());
        player.openInventory(inv);
    }

    public static void openAchievementsGUI(AdvancedPaintBattle plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§d§lLogros");
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, ItemUtil.createFiller(Material.PURPLE_STAINED_GLASS_PANE));
        }
        List<String> achievements = plugin.getAchievementManager().getAchievements(player.getUniqueId());
        inv.setItem(4, ItemUtil.createInfo("§d§lLogros",
                "§7Desbloqueados: §e" + achievements.size(),
                "§7Completa objetivos para desbloquear"
        ));
        int slot = 9;
        for (String ach : achievements) {
            if (slot >= 45) break;
            inv.setItem(slot, ItemUtil.createGlowing(Material.NETHER_STAR, "§e§l" + ach, "§7¡Logro desbloqueado!"));
            slot++;
        }
        inv.setItem(49, ItemUtil.createClose());
        player.openInventory(inv);
    }

    public static void openGalleryGUI(AdvancedPaintBattle plugin, Player player) {
        new GalleryGUI(plugin).open(player);
    }

    public static void openFriendGUI(AdvancedPaintBattle plugin, Player player) {
        new FriendGUI(plugin).open(player);
    }

    public static void openTournamentGUI(AdvancedPaintBattle plugin, Player player) {
        new TournamentGUI(plugin).open(player);
    }

    public static void openBattlePassGUI(AdvancedPaintBattle plugin, Player player) {
        new BattlePassGUI(plugin).open(player);
    }

    public static void openProfileGUI(AdvancedPaintBattle plugin, Player viewer, Player target) {
        new ProfileGUI(plugin).open(viewer, target);
    }
}
