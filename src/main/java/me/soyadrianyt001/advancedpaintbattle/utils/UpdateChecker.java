package me.soyadrianyt001.advancedpaintbattle.utils;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateChecker implements Listener {

    private final AdvancedPaintBattle plugin;
    private final String currentVersion;
    private String latestVersion = null;
    private boolean updateAvailable = false;

    public UpdateChecker(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void check() {
        if (!plugin.getConfig().getBoolean("update-checker.enabled", true)) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                latestVersion = currentVersion;
                if (!currentVersion.equals(latestVersion)) {
                    updateAvailable = true;
                    notifyConsole();
                }
            } catch (Exception e) {
                plugin.getLogger().info("[APB] No se pudo verificar actualizaciones.");
            }
        });
    }

    private void notifyConsole() {
        Bukkit.getConsoleSender().sendMessage("§6§l╔══════════════════════════════════╗");
        Bukkit.getConsoleSender().sendMessage("§6§l║  §c§l⚠ ACTUALIZACIÓN DISPONIBLE     §6§l║");
        Bukkit.getConsoleSender().sendMessage("§6§l║  §7Actual: §cv" + currentVersion + "              §6§l║");
        Bukkit.getConsoleSender().sendMessage("§6§l║  §7Nueva:  §av" + latestVersion + "              §6§l║");
        Bukkit.getConsoleSender().sendMessage("§6§l╚══════════════════════════════════╝");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!updateAvailable) return;
        if (!player.hasPermission("advancedpaintbattle.admin")) return;
        if (!plugin.getConfig().getBoolean("update-checker.notify-admins", true)) return;
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                plugin.getMessageManager().sendRaw(player, "update-available",
                        "%current%", currentVersion, "%new%", latestVersion), 40L);
    }

    public boolean isUpdateAvailable() { return updateAvailable; }
    public String getLatestVersion() { return latestVersion; }
    public String getCurrentVersion() { return currentVersion; }
}
