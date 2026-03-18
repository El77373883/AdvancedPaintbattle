package me.soyadrianyt001.advancedpaintbattle.listeners;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GamePlayer;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class GameListener implements Listener {

    private final AdvancedPaintBattle plugin;

    public GameListener(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        String arenaName = plugin.getGameManager().getPlayerArena(player.getUniqueId());
        GameSession session = plugin.getGameManager().getSession(arenaName);
        if (session == null) return;
        GamePlayer gp = session.getGamePlayer(player.getUniqueId());
        if (gp == null) return;
        e.setCancelled(true);
        // Undo con F
        if (!gp.getUndoStack().isEmpty()) {
            var data = gp.getUndoStack().pop();
            org.bukkit.Location loc = (org.bukkit.Location) data.get("loc");
            Material mat = (Material) data.get("mat");
            loc.getBlock().setType(mat);
            player.sendMessage("§e↩ Deshecho.");
        }
    }
}
