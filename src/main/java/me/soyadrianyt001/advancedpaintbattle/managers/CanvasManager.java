package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.Arena;
import me.soyadrianyt001.advancedpaintbattle.models.GamePlayer;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.List;

public class CanvasManager {

    public static void buildCanvases(AdvancedPaintBattle plugin, GameSession session, Arena arena) {
        int size = plugin.getConfig().getInt("canvas-size", 16);
        int spacing = plugin.getConfig().getInt("canvas-spacing", 6);
        String floorStr = plugin.getConfig().getString("canvas-floor", "WHITE_CONCRETE");
        String borderStr = plugin.getConfig().getString("canvas-border", "GRAY_CONCRETE");
        Material floor = Material.getMaterial(floorStr) != null ? Material.getMaterial(floorStr) : Material.WHITE_CONCRETE;
        Material border = Material.getMaterial(borderStr) != null ? Material.getMaterial(borderStr) : Material.GRAY_CONCRETE;

        Location origin = arena.getCanvasOrigin();
        if (origin == null) return;

        List<GamePlayer> players = session.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            GamePlayer gp = players.get(i);
            int offsetX = i * (size + spacing);
            Location canvasStart = origin.clone().add(offsetX, 0, 0);
            session.getCanvasLocations().put(gp.getUuid(), canvasStart);

            for (int x = -1; x <= size; x++) {
                for (int z = -1; z <= size; z++) {
                    Location loc = canvasStart.clone().add(x, 0, z);
                    if (x == -1 || x == size || z == -1 || z == size) {
                        loc.getBlock().setType(border);
                    } else {
                        loc.getBlock().setType(floor);
                    }
                }
            }

            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null) {
                Location center = canvasStart.clone().add(size / 2.0, 1, size / 2.0);
                p.teleport(center);
            }
        }
    }

    public static void clearCanvases(AdvancedPaintBattle plugin, GameSession session, Arena arena) {
        if (arena == null) return;
        int size = plugin.getConfig().getInt("canvas-size", 16);
        int spacing = plugin.getConfig().getInt("canvas-spacing", 6);
        Location origin = arena.getCanvasOrigin();
        if (origin == null) return;
        int count = session.getPlayers().size();
        for (int i = 0; i < count; i++) {
            int offsetX = i * (size + spacing);
            Location canvasStart = origin.clone().add(offsetX, 0, 0);
            for (int x = -1; x <= size; x++) {
                for (int z = -1; z <= size; z++) {
                    canvasStart.clone().add(x, 0, z).getBlock().setType(Material.AIR);
                }
            }
        }
    }

    public static boolean isOnCanvas(Location loc, Location canvasStart, int size) {
        if (!loc.getWorld().equals(canvasStart.getWorld())) return false;
        int dx = loc.getBlockX() - canvasStart.getBlockX();
        int dz = loc.getBlockZ() - canvasStart.getBlockZ();
        return dx >= 0 && dx < size && dz >= 0 && dz < size;
    }
}
