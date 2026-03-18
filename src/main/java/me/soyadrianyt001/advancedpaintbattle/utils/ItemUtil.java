package me.soyadrianyt001.advancedpaintbattle.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class ItemUtil {

    public static ItemStack create(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(name.replace("&", "§"));
        if (lore.length > 0) {
            List<String> loreList = Arrays.stream(lore)
                    .map(l -> l.replace("&", "§"))
                    .toList();
            meta.setLore(loreList);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createGlowing(Material material, String name, String... lore) {
        ItemStack item = create(material, name, lore);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.addEnchant(Enchantment.INFINITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSkull(String playerName, String displayName, String... lore) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null) return skull;
        meta.setOwnerProfile(org.bukkit.Bukkit.createPlayerProfile(playerName));
        meta.setDisplayName(displayName.replace("&", "§"));
        if (lore.length > 0) {
            List<String> loreList = Arrays.stream(lore)
                    .map(l -> l.replace("&", "§"))
                    .toList();
            meta.setLore(loreList);
        }
        skull.setItemMeta(meta);
        return skull;
    }

    public static ItemStack createFiller(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName("§r");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createBack() {
        return create(Material.ARROW, "&c&lVolver", "&7Click para volver");
    }

    public static ItemStack createClose() {
        return create(Material.BARRIER, "&c&lCerrar", "&7Click para cerrar");
    }

    public static ItemStack createInfo(String name, String... lore) {
        return createGlowing(Material.NETHER_STAR, name, lore);
    }

    public static boolean isNullOrAir(ItemStack item) {
        return item == null || item.getType().isAir();
    }
}
