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

        // Notificar update si es admin
        if (player.hasPermission("advancedpaintbattle.admin")) {
            int pending = plugin.getReportManager().getPendingCount();
            if (pending > 0) {
                player.sendMessage("§c§l⚠ §eTienes §c" + pending + " §ereportes pendientes. Usa §e/apb report list");
            }
            plugin.getAdminLogger().log("Admin " + player.getName() + " se conecto al servidor.");
        }

        // Particulas de bienvenida
        if (plugin.getConfigManager().isParticlesEnabled()) {
            EffectUtil.spawnCountdownParticles(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e​​​​​​​​​​​​​​​​
