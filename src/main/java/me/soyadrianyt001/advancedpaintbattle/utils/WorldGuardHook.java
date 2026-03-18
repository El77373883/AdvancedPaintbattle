package me.soyadrianyt001.advancedpaintbattle.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
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
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));
            if (regions == null) return true;
            com.sk89q.worldguard.LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            com.sk89q.worldedit.util.Location wgLoc = BukkitAdapter.adapt(location);
            return WorldGuard.getInstance().getPlatform().getSessionManager()
                    .hasBypass(localPlayer, BukkitAdapter.adapt(location.getWorld()));
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isInRegion(Location location, String regionId) {
        if (!enabled) return false;
        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));
            if (regions == null) return false;
            ProtectedRegion region = regions.getRegion(regionId);
            if (region == null) return false;
            return region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        } catch (Exception e) {
            return false;
        }
    }
}
