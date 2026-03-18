package me.soyadrianyt001.advancedpaintbattle.utils;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy economy;

    public static boolean setup() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    public static void giveMoney(org.bukkit.entity.Player player, double amount) {
        if (economy != null) economy.depositPlayer(player, amount);
    }

    public static double getBalance(org.bukkit.entity.Player player) {
        if (economy != null) return economy.getBalance(player);
        return 0;
    }

    public static boolean isEnabled() { return economy != null; }
}
