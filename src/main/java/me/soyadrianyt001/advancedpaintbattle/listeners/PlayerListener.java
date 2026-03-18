package me.soyadrianyt001.advancedpaintbattle.listeners;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.utils.EffectUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final AdvancedPaintBattle plugin;

    public PlayerListener(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        // Cargar datos del jugador
        plugin.getPlayerDataManager().loadPlayer(player);

        // Primer join
        if (!plugin.getPlayerDataManager().isRegistered(player.getUniqueId())) {
            plugin.getDataManager().incrementTotalPlayers();
        }

        // Mensaje de bienvenida
        plugin.getMessageManager().sendRaw(player, "welcome", "%player%", player.getName());

        // Notificar si es admin
        if (player.hasPermission("advancedpaintbattle.admin")) {
            int pending = plugin.getReportManager().getPendingCount();
            if (pending > 0) {
                player.sendMessage("§c§l⚠ §eTienes §c" + pending +
                        " §ereportes pendientes. Usa §e/apb report list");
            }
            plugin.getAdminLogger().log("Admin " + player.getName() + " se conecto.");
        }

        // Particulas de bienvenida
        if (plugin.getConfigManager().isParticlesEnabled()) {
            EffectUtil.spawnCountdownParticles(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        // Guardar datos
        plugin.getPlayerDataManager().savePlayer(player);

        // Salir de arena si estaba en juego
        if (plugin.getGameManager().isInGame(player.getUniqueId())) {
            plugin.getGameManager().leaveArena(player);
        }

        // Limpiar violations del anticheat
        plugin.getAntiCheatListener().clearViolations(player.getUniqueId());
    }
}
