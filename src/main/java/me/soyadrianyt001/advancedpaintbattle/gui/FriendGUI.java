package me.soyadrianyt001.advancedpaintbattle.gui;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;

public class FriendGUI {

    private final AdvancedPaintBattle plugin;

    public FriendGUI(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§b§l👥 Amigos");

        // Relleno
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, ItemUtil.createFiller(Material.CYAN_STAINED_GLASS_PANE));
        }

        // Info
        inv.setItem(4, ItemUtil.createInfo("§b§lTus Amigos",
                "§7Gestiona tus amigos aqui",
                "",
                "§eClick derecho §7para eliminar amigo"
        ));

        List<String> friends = plugin.getFriendManager().getFriends(player.getUniqueId());
        int slot = 10;
        for (String uuidStr : friends) {
            if (slot >= 44) break;
            try {
                UUID uuid = UUID.fromString(uuidStr);
                OfflinePlayer friend = Bukkit.getOfflinePlayer(uuid);
                boolean online = friend.isOnline();
                String status = online ? "§aEn linea" : "§cDesconectado";
                long lastSeen = plugin.getPlayerDataManager().getLastSeen(uuid);
                String seen = lastSeen > 0 ? me.soyadrianyt001.advancedpaintbattle.utils.TimeUtil.timeSince(lastSeen) : "Nunca";

                inv.setItem(slot, ItemUtil.createSkull(
                        friend.getName() != null ? friend.getName() : "Desconocido",
                        "§f§l" + (friend.getName() != null ? friend.getName() : "Desconocido"),
                        "§7Estado: " + status,
                        "§7Ultima vez: §f" + seen,
                        "",
                        "§cClick derecho para eliminar"
                ));
                slot++;
                if (slot == 17 || slot == 26 || slot == 35) slot += 2;
            } catch (Exception ignored) {}
        }

        if (friends.isEmpty()) {
            inv.setItem(22, ItemUtil.create(Material.BARRIER,
                    "§cNo tienes amigos aun",
                    "§7Usa §e/apb friend add <jugador>",
                    "§7para agregar amigos"
            ));
        }

        // Agregar amigo
        inv.setItem(48, ItemUtil.create(Material.LIME_DYE,
                "§a§lAgregar Amigo",
                "§7Usa §e/apb friend add <jugador>"
        ));

        // Cerrar
        inv.setItem(50, ItemUtil.createClose());

        player.openInventory(inv);
    }
}
