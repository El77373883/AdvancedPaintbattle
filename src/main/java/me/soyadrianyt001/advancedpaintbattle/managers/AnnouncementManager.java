package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class AnnouncementManager {

    private final AdvancedPaintBattle plugin;

    public AnnouncementManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    public void startAnnouncing() {
        if (!plugin.getConfig().getBoolean("announcements.enabled", true)) return;
        int interval = plugin.getConfig().getInt("announcements.interval", 300);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage("§6§l╔══════════════════════════════════╗");
                Bukkit.broadcastMessage("§6§l║  §e§lAdvancedPaintBattle §6esta activo!  §6§l║");
                Bukkit.broadcastMessage("§6§l║  §7Usa §e/apb join §7para jugar         §6§l║");
                Bukkit.broadcastMessage("§6§l╚══════════════════════════════════╝");
            }
        }.runTaskTimer(plugin, interval * 20L, interval * 20L);
    }
}
