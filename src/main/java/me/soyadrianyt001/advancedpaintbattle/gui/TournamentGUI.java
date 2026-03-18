package me.soyadrianyt001.advancedpaintbattle.gui;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TournamentGUI {

    private final AdvancedPaintBattle plugin;

    public TournamentGUI(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6§l🏆 Torneo");

        // Relleno
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, ItemUtil.createFiller(Material.YELLOW_STAINED_GLASS_PANE));
        }

        boolean active = plugin.getTournamentManager().isActive();

        // Info principal
        inv.setItem(4, ItemUtil.createInfo("§6§lTorneo Semanal",
                active ? "§aActivo ahora!" : "§cNo hay torneo activo",
                "",
                "§7Los mejores jugadores compiten",
                "§7por premios exclusivos"
        ));

        if (active) {
            // Bracket visual
            inv.setItem(19, ItemUtil.create(Material.GOLD_BLOCK,
                    "§6§lBracket",
                    "§7Ver el bracket actual"
            ));
            inv.setItem(21, ItemUtil.create(Material.DIAMOND,
                    "§b§lRecompensas",
                    "§7§lPrimer lugar:",
                    "§e500 monedas + Trofeo",
                    "§7§lSegundo lugar:",
                    "§e300 monedas",
                    "§7§lTercer lugar:",
                    "§e150 monedas"
            ));
            inv.setItem(23, ItemUtil.create(Material.PAPER,
                    "§e§lMis Stats de Torneo",
                    "§7Victorias: §e" + plugin.getTournamentManager().getScore(player.getUniqueId()),
                    "§7Posicion: §e" + plugin.getTournamentManager().getPosition(player.getUniqueId())
            ));
            inv.setItem(25, ItemUtil.create(Material.LIME_WOOL,
                    "§a§lUnirse al Torneo",
                    "§7Click para participar"
            ));
        } else {
            inv.setItem(22, ItemUtil.create(Material.CLOCK,
                    "§e§lProximo Torneo",
                    "§7El torneo se realiza",
                    "§7cada semana automaticamente",
                    "",
                    "§7Mantente atento!"
            ));
        }

        // Admin
        if (player.hasPermission("advancedpaintbattle.admin")) {
            inv.setItem(45, ItemUtil.create(Material.COMMAND_BLOCK,
                    "§c§lAdmin: Iniciar Torneo",
                    "§7Fuerza el inicio del torneo"
            ));
            inv.setItem(46, ItemUtil.create(Material.REDSTONE_BLOCK,
                    "§c§lAdmin: Terminar Torneo",
                    "§7Fuerza el fin del torneo"
            ));
        }

        inv.setItem(49, ItemUtil.createClose());
        player.openInventory(inv);
    }
}
