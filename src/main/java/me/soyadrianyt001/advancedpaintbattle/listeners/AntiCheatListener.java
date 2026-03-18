package me.soyadrianyt001.advancedpaintbattle.listeners;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AntiCheatListener implements Listener {

    private final AdvancedPaintBattle plugin;
    private final Map<UUID, Long> lastCommand = new HashMap<>();

    public AntiCheatListener(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        long now = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();
        if (lastCommand.containsKey(uuid) && now - lastCommand.get(uuid) < 500) {
            e.setCancelled(true);
            player.sendMessage("§c¡Muy rapido! Espera un momento.");
            return;
        }
        lastCommand.put(uuid, now);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) e.setCancelled(true);
    }
}
