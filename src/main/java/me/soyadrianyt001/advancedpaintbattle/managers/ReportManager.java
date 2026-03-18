package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReportManager {

    private final AdvancedPaintBattle plugin;
    private final FileUtil fileUtil;
    private FileConfiguration reportConfig;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN = 60000; // 1 minuto

    public ReportManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        this.fileUtil = new FileUtil(plugin);
        load();
    }

    private void load() {
        reportConfig = fileUtil.loadConfig("reports.yml");
    }

    public boolean report(Player reporter, Player reported, String reason) {
        // Cooldown
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(reporter.getUniqueId())) {
            long diff = now - cooldowns.get(reporter.getUniqueId());
            if (diff < COOLDOWN) {
                long remaining = (COOLDOWN - diff) / 1000;
                reporter.sendMessage("§c§l✗ §cEspera §e" + remaining + "s §cantes de reportar de nuevo.");
                return false;
            }
        }

        cooldowns.put(reporter.getUniqueId(), now);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String path = "reports." + id;

        reportConfig.set(path + ".reporter", reporter.getName());
        reportConfig.set(path + ".reported", reported.getName());
        reportConfig.set(path + ".reason", reason);
        reportConfig.set(path + ".date", date);
        reportConfig.set(path + ".status", "PENDIENTE");
        reportConfig.set(path + ".arena", plugin.getGameManager().getPlayerArena(reported.getUniqueId()));
        fileUtil.saveConfig(reportConfig, "reports.yml");

        // Notificar admins
        reporter.sendMessage("§a§l✔ §eReporte enviado. ID: §f#" + id);
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("advancedpaintbattle.admin"))
                .forEach(admin -> {
                    admin.sendMessage("§6§l╔══════ NUEVO REPORTE ══════╗");
                    admin.sendMessage("§7ID: §e#" + id);
                    admin.sendMessage("§7Reportado: §c" + reported.getName());
                    admin.sendMessage("§7Reportador: §f" + reporter.getName());
                    admin.sendMessage("§7Razon: §f" + reason);
                    admin.sendMessage("§7Usa §e/apb report check " + id + " §7para ver detalles");
                    admin.sendMessage("§6§l╚══════════════════════════╝");
                });

        plugin.getLogger().info("[APB-REPORT] " + reporter.getName() +
                " reporto a " + reported.getName() + " por: " + reason + " [#" + id + "]");
        return true;
    }

    public void resolveReport(String id, Player admin, String resolution) {
        String path = "reports." + id;
        if (!reportConfig.contains(path)) {
            admin.sendMessage("§c§l✗ §cReporte #" + id + " no encontrado.");
            return;
        }
        reportConfig.set(path + ".status", "RESUELTO");
        reportConfig.set(path + ".resolved_by", admin.getName());
        reportConfig.set(path + ".resolution", resolution);
        fileUtil.saveConfig(reportConfig, "reports.yml");
        admin.sendMessage("§a§l✔ §eReporte §f#" + id + " §eresuelto.");
        plugin.getAdminLogger().log(admin, "Resolvio reporte #" + id + ": " + resolution);
    }

    public void showReport(Player admin, String id) {
        String path = "reports." + id;
        if (!reportConfig.contains(path)) {
            admin.sendMessage("§c§l✗ §cReporte #" + id + " no encontrado.");
            return;
        }
        admin.sendMessage("§6§l╔══════ REPORTE #" + id + " ══════╗");
        admin.sendMessage("§7Reportado: §c" + reportConfig.getString(path + ".reported"));
        admin.sendMessage("§7Reportador: §f" + reportConfig.getString(path + ".reporter"));
        admin.sendMessage("§7Razon: §f" + reportConfig.getString(path + ".reason"));
        admin.sendMessage("§7Fecha: §f" + reportConfig.getString(path + ".date"));
        admin.sendMessage("§7Arena: §f" + reportConfig.getString(path + ".arena"));
        admin.sendMessage("§7Estado: §e" + reportConfig.getString(path + ".status"));
        if (reportConfig.contains(path + ".resolved_by")) {
            admin.sendMessage("§7Resuelto por: §a" + reportConfig.getString(path + ".resolved_by"));
            admin.sendMessage("§7Resolucion: §f" + reportConfig.getString(path + ".resolution"));
        }
        admin.sendMessage("§6§l╚══════════════════════════╝");
    }

    public List<String> getPendingReports() {
        List<String> pending = new ArrayList<>();
        if (reportConfig.getConfigurationSection("reports") == null) return pending;
        for (String id : reportConfig.getConfigurationSection("reports").getKeys(false)) {
            if ("PENDIENTE".equals(reportConfig.getString("reports." + id + ".status"))) {
                pending.add(id);
            }
        }
        return pending;
    }

    public int getPendingCount() { return getPendingReports().size(); }
}
