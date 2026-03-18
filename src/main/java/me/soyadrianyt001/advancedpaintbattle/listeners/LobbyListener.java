package me.soyadrianyt001.advancedpaintbattle.listeners;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class LobbyListener implements Listener {

    private final AdvancedPaintBattle plugin;

    public LobbyListener(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof org.bukkit.entity.Player player)) return;
        if (plugin.getGameManager().isInGame(player.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (plugin.getGameManager().isInGame(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }
}
