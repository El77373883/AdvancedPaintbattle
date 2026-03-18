package me.soyadrianyt001.advancedpaintbattle.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class LocationUtil {

    public static String serialize(Location loc) {
        if (loc == null || loc.getWorld() == null) return null;
        return loc.getWorld().getName() + "," +
                loc.getX() + "," +
                loc.getY() + "," +
                loc.getZ() + "," +
                loc.getYaw() + "," +
                loc.getPitch();
    }

    public static Location deserialize(String str) {
        if (str == null || str.isEmpty()) return null;
        try {
            String[] parts = str.split(",");
            World world = Bukkit.getWorld(parts[0]);
            if (world == null) return null;
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);
            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            return null;
        }
    }

    public static void saveLocation(FileConfiguration config, String path, Location loc) {
        config.set(path, serialize(loc));
    }

    public static Location loadLocation(FileConfiguration config, String path) {
        return deserialize(config.getString(path));
    }

    public static double distance(Location a, Location b) {
        if (!a.getWorld().equals(b.getWorld())) return -1;
        return a.distance(b);
    }

    public static Location center(Location loc) {
        return new Location(
                loc.getWorld(),
                loc.getBlockX() + 0.5,
                loc.getBlockY(),
                loc.getBlockZ() + 0.5,
                loc.getYaw(),
                loc.getPitch()
        );
    }
}
