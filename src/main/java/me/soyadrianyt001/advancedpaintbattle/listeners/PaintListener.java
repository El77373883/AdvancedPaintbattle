package me.soyadrianyt001.advancedpaintbattle.listeners;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.managers.CanvasManager;
import me.soyadrianyt001.advancedpaintbattle.models.GamePlayer;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import me.soyadrianyt001.advancedpaintbattle.utils.EffectUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PaintListener implements Listener {

    private final AdvancedPaintBattle plugin;

    public PaintListener(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        String arenaName = plugin.getGameManager().getPlayerArena(player.getUniqueId());
        GameSession session = plugin.getGameManager().getSession(arenaName);
        if (session == null || session.getState() != GameSession.GameState.PAINTING) return;

        GamePlayer gp = session.getGamePlayer(player.getUniqueId());
        if (gp == null) return;

        Location canvasLoc = session.getCanvasLocations().get(player.getUniqueId());
        if (canvasLoc == null) return;

        int size = plugin.getConfig().getInt("canvas-size", 16);
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType().isAir()) return;

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block clicked = e.getClickedBlock();
            if (clicked == null) return;

            if (!CanvasManager.isOnCanvas(clicked.getLocation(), canvasLoc, size)) {
                e.setCancelled(true);
                return;
            }

            e.setCancelled(true);
            String brush = gp.getSelectedBrush();
            Material paintMat = hand.getType();

            // Guardar para undo
            Map<String, Object> undoData = Map.of(
                    "loc", clicked.getLocation().clone(),
                    "mat", clicked.getType()
            );
            gp.getUndoStack().push(undoData);

            switch (brush) {
                case "ERASER":
                    paintMat = Material.getMaterial(plugin.getConfig().getString("canvas-floor", "WHITE_CONCRETE"));
                    break;
                case "BIG_3x3":
                    paintArea(player, clicked.getLocation(), paintMat, canvasLoc, size, 1);
                    return;
                case "BIG_5x5":
                    paintArea(player, clicked.getLocation(), paintMat, canvasLoc, size, 2);
                    return;
                case "RAINBOW":
                    paintMat = getRainbowMaterial();
                    break;
                case "FILL":
                    floodFill(clicked.getLocation(), clicked.getType(), paintMat, canvasLoc, size, 0);
                    return;
                case "MIRROR":
                    paintMirror(player, clicked.getLocation(), paintMat, canvasLoc, size);
                    return;
            }

            clicked.setType(paintMat);
            EffectUtil.spawnPaintParticles(player, Color.YELLOW);
        }
    }

    private void paintArea(Player player, Location center, Material mat, Location canvasLoc, int size, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                Location loc = center.clone().add(dx, 0, dz);
                if (CanvasManager.isOnCanvas(loc, canvasLoc, size)) {
                    loc.getBlock().setType(mat);
                }
            }
        }
        EffectUtil.spawnPaintParticles(player, Color.ORANGE);
    }

    private void floodFill(Location loc, Material original, Material replacement, Location canvasLoc, int size, int depth) {
        if (depth > 200) return;
        if (!CanvasManager.isOnCanvas(loc, canvasLoc, size)) return;
        Block block = loc.getBlock();
        if (block.getType() != original || block.getType() == replacement) return;
        block.setType(replacement);
        floodFill(loc.clone().add(1, 0, 0), original, replacement, canvasLoc, size, depth + 1);
        floodFill(loc.clone().add(-1, 0, 0), original, replacement, canvasLoc, size, depth + 1);
        floodFill(loc.clone().add(0, 0, 1), original, replacement, canvasLoc, size, depth + 1);
        floodFill(loc.clone().add(0, 0, -1), original, replacement, canvasLoc, size, depth + 1);
    }

    private void paintMirror(Player player, Location loc, Material mat, Location canvasLoc, int size) {
        loc.getBlock().setType(mat);
        int dx = loc.getBlockX() - canvasLoc.getBlockX();
        Location mirror = canvasLoc.clone().add(size - 1 - dx, 0, loc.getBlockZ() - canvasLoc.getBlockZ());
        if (CanvasManager.isOnCanvas(mirror, canvasLoc, size)) mirror.getBlock().setType(mat);
        EffectUtil.spawnPaintParticles(player, Color.AQUA);
    }

    private Material getRainbowMaterial() {
        Material[] colors = {
                Material.RED_CONCRETE, Material.ORANGE_CONCRETE, Material.YELLOW_CONCRETE,
                Material.LIME_CONCRETE, Material.CYAN_CONCRETE, Material.BLUE_CONCRETE,
                Material.PURPLE_CONCRETE, Material.MAGENTA_CONCRETE, Material.PINK_CONCRETE
        };
        return colors[(int) (Math.random() * colors.length)];
    }
}
