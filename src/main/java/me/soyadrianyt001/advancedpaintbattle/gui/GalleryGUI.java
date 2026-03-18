package me.soyadrianyt001.advancedpaintbattle.gui;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;

public class GalleryGUI {

    private final AdvancedPaintBattle plugin;

    public GalleryGUI(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6§l🖼 Galería de Dibujos");

        // Relleno decorativo
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, ItemUtil.createFiller(Material.GRAY_STAINED_GLASS_PANE));
        }

        List<Map<String, Object>> top = plugin.getGalleryManager().getTopDrawings(28);
        int slot = 10;
        for (Map<String, Object> entry : top) {
            if (slot >= 44) break;
            String pName = (String) entry.get("player");
            String theme = (String) entry.get("theme");
            int votes = (int) entry.get("votes");
            int likes = (int) entry.get("likes");
            String id = (String) entry.get("id");

            inv.setItem(slot, ItemUtil.createSkull(pName,
                    "§e§l" + pName,
                    "§7Tema: §f" + theme,
                    "§7Votos: §e" + votes,
                    "§7Likes: §d" + likes,
                    "",
                    "§aClick para dar like!"
            ));
            slot++;
            if (slot == 17 || slot == 26 || slot == 35) slot += 2;
        }

        // Info
        inv.setItem(4, ItemUtil.createInfo("§6§lGalería",
                "§7Los mejores dibujos del servidor",
                "§7ordenados por likes",
                "",
                "§eTotal dibujos: §f" + top.size()
        ));

        // Cerrar
        inv.setItem(49, ItemUtil.createClose());

        player.openInventory(inv);
    }
}
