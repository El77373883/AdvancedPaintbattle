package me.soyadrianyt001.advancedpaintbattle.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook {

    private static boolean enabled = false;

    public static void setup() {
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard");
            enabled = true;
        } catch (ClassNotFoundException e) {
            enabled = false;
        }
    }

    public static boolean isEnabled() { return enabled; }

    public static boolean canBuild(Player player, Location location) {
        if (!enabled) return true;
        try {
            // Usar reflection para evitar errores de compilacion
            Object worldGuard = Class.forName("com.sk89q.worldguard.WorldGuard")
                    .getMethod("getInstance").invoke(null);
            return true; // Si WorldGuard esta presente, permitir por defecto
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isInRegion(Location location, String regionId) {
        if (!enabled) return false;
        try {
            Object worldGuard = Class.forName("com.sk89q.worldguard.WorldGuard")
                    .getMethod("getInstance").invoke(null);
            Object platform = worldGuard.getClass().getMethod("getPlatform").invoke(worldGuard);
            Object container = platform.getClass().getMethod("getRegionContainer").invoke(platform);
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
