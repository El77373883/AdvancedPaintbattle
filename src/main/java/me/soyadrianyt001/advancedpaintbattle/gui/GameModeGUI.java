package me.soyadrianyt001.advancedpaintbattle.gui;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GameModeGUI {

    private final AdvancedPaintBattle plugin;

    public GameModeGUI(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6§lSeleccionar Modo");

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, ItemUtil.createFiller(Material.GRAY_STAINED_GLASS_PANE));
        }

        inv.setItem(4, ItemUtil.createInfo("§6§lModos de Juego",
                "§7Selecciona el modo para tu partida"
        ));

        inv.setItem(19, ItemUtil.createGlowing(Material.PAINTING,
                "§f§lNormal",
                "§7El modo clasico de Paint Battle",
                "§aTodos disponible"
        ));
        inv.setItem(20, ItemUtil.createGlowing(Material.ENDER_EYE,
                "§c§lModo Ciego",
                "§7El tema se revela a la mitad",
                "§aTodos disponible"
        ));
        inv.setItem(21, ItemUtil.createGlowing(Material.LIGHTNING_ROD,
                "§5§lModo Caos",
                "§7El tema cambia cada 20 segundos",
                "§aTodos disponible"
        ));
        inv.setItem(22, ItemUtil.createGlowing(Material.FIREWORK_ROCKET,
                "§e§lRapidfire",
                "§710 temas, 15 segundos cada uno",
                "§aTodos disponible"
        ));
        inv.setItem(23, ItemUtil.createGlowing(Material.LIME_WOOL,
                "§a§lColaborativo",
                "§7Todos pintan en un lienzo gigante",
                "§aTodos disponible"
        ));
        inv.setItem(24, ItemUtil.createGlowing(Material.IRON_SWORD,
                "§c§lBatalla",
                "§7Puedes destruir el lienzo rival",
                "§aTodos disponible"
        ));
        inv.setItem(25, ItemUtil.createGlowing(Material.SHIELD,
                "§9§lEquipos 2v2v2",
                "§7Equipos pintan juntos",
                "§7Min 6 jugadores"
        ));

        inv.setItem(49, ItemUtil.createClose());
        player.openInventory(inv);
    }
}
