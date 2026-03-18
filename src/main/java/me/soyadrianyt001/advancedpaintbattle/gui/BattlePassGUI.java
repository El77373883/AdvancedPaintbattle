package me.soyadrianyt001.advancedpaintbattle.gui;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BattlePassGUI {

    private final AdvancedPaintBattle plugin;

    public BattlePassGUI(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§d§l🎫 Pase de Batalla");

        // Relleno
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, ItemUtil.createFiller(Material.PURPLE_STAINED_GLASS_PANE));
        }

        int level = plugin.getBattlePassManager().getLevel(player.getUniqueId());
        int xp = plugin.getBattlePassManager().getXP(player.getUniqueId());
        int required = level * 100;

        // Info
        inv.setItem(4, ItemUtil.createInfo("§d§lPase de Batalla",
                "§7Temporada §e" + plugin.getDataManager().getCurrentSeason(),
                "",
                "§7Nivel actual: §d" + level,
                "§7XP: §e" + xp + "§7/§e" + required,
                "",
                "§7Juega partidas para ganar XP"
        ));

        // Barra de progreso visual
        int progress = (int) ((double) xp / required * 9);
        for (int i = 0; i < 9; i++) {
            Material mat = i < progress ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
            inv.setItem(i + 9, ItemUtil.createFiller(mat));
        }

        // Recompensas por nivel
        int[][] rewardSlots = {{19, 1}, {20, 5}, {21, 10}, {22, 15}, {23, 20},
                {24, 25}, {25, 30}, {28, 35}, {29, 40}, {30, 45},
                {31, 50}, {32, 55}, {33, 60}, {34, 65}};

        Material[] rewards = {
                Material.SUNFLOWER, Material.GOLD_INGOT, Material.DIAMOND,
                Material.EMERALD, Material.NETHER_STAR, Material.BEACON,
                Material.TOTEM_OF_UNDYING, Material.DRAGON_EGG
        };
        int[] coinRewards = {50, 100, 150, 200, 300, 400, 500, 1000};

        for (int i = 0; i < rewardSlots.length; i++) {
            int slot = rewardSlots[i][0];
            int reqLevel = rewardSlots[i][1];
            Material mat = i < rewards.length ? rewards[i] : Material.GOLD_BLOCK;
            int coins = i < coinRewards.length ? coinRewards[i] : 100;
            boolean unlocked = level >= reqLevel;

            inv.setItem(slot, ItemUtil.create(
                    unlocked ? mat : Material.GRAY_STAINED_GLASS_PANE,
                    unlocked ? "§a§l✔ Nivel " + reqLevel : "§c§l🔒 Nivel " + reqLevel,
                    unlocked ? "§7¡Recompensa desbloqueada!" : "§7Alcanza nivel §e" + reqLevel,
                    "§6+" + coins + " monedas"
            ));
        }

        inv.setItem(49, ItemUtil.createClose());
        player.openInventory(inv);
    }
}
