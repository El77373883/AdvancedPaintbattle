package me.soyadrianyt001.advancedpaintbattle.listeners;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AntiCheatListener implements Listener {

    private final AdvancedPaintBattle plugin;
    private final Map<UUID, Long> lastCommand = new HashMap<>();
    private final Map<UUID, Long> lastInteract = new HashMap<>();
    private final Map<UUID, Integer> violations = new HashMap<>();
    private static final long COMMAND_COOLDOWN = 500;
    private static final long INTERACT_COOLDOWN = 50;
    private static final int MAX_VIOLATIONS = 10;

    public AntiCheatListener(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        if (player.hasPermission("advancedpaintbattle.bypass")) return;

        long now = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();

        if (lastCommand.containsKey(uuid) && now - lastCommand.get(uuid) < COMMAND_COOLDOWN) {
            e.setCancelled(true);
            addViolation(player, "SPAM_COMMANDS");
            return;
        }
        lastCommand.put(uuid, now);

        String cmd = e.getMessage().toLowerCase();
        String[] blocked = {"/tp", "/teleport", "/fly", "/gamemode", "/gm",
                "/give", "/item", "/god", "/heal", "/effect"};
        for (String b : blocked) {
            if (cmd.startsWith(b) && !player.hasPermission("advancedpaintbattle.admin")) {
                e.setCancelled(true);
                player.sendMessage("§c§l✗ §cNo puedes usar ese comando durante la partida.");
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        if (player.hasPermission("advancedpaintbattle.bypass")) return;
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL ||
            e.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) {
            e.setCancelled(true);
            addViolation(player, "ILLEGAL_TELEPORT");
        }
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        if (player.hasPermission("advancedpaintbattle.bypass")) return;
        if (player.getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
            addViolation(player, "ILLEGAL_FLY");
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        e.setCancelled(true);
        player.setFoodLevel(20);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        String title = e.getView().getTitle();
        if (!title.contains("AdvancedPaintBattle") &&
            !title.contains("Paleta") &&
            !title.contains("Vota") &&
            !title.contains("Tienda") &&
            !title.contains("Logros") &&
            !title.contains("Misiones") &&
            !title.contains("Perfil") &&
            !title.contains("Amigos") &&
            !title.contains("Galería") &&
            !title.contains("Torneo") &&
            !title.contains("Pase") &&
            !title.contains("Modo")) {
            // Permitir inventario propio
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;

        long now = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();

        if (lastInteract.containsKey(uuid) && now - lastInteract.get(uuid) < INTERACT_COOLDOWN) {
            addViolation(player, "FAST_INTERACT");
            return;
        }
        lastInteract.put(uuid, now);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (plugin.getGameManager().isInGame(player.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (plugin.getGameManager().isInGame(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getGameManager().isInGame(player.getUniqueId())) return;
        String msg = e.getMessage();
        if (msg.length() > 100) {
            e.setCancelled(true);
            player.sendMessage("§c§l✗ §cMensaje demasiado largo.");
        }
    }

    private void addViolation(Player player, String type) {
        UUID uuid = player.getUniqueId();
        int count = violations.getOrDefault(uuid, 0) + 1;
        violations.put(uuid, count);

        plugin.getLogger().warning("[APB-ANTICHEAT] " + player.getName() +
                " - Violacion: " + type + " (" + count + "/" + MAX_VIOLATIONS + ")");

        if (count >= MAX_VIOLATIONS) {
            violations.remove(uuid);
            kickPlayer(player, type);
        } else if (count >= MAX_VIOLATIONS / 2) {
            player.sendMessage("§c§l⚠ §cComportamiento sospechoso. (" + count + "/" + MAX_VIOLATIONS + ")");
            Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.hasPermission("advancedpaintbattle.admin"))
                    .forEach(admin -> admin.sendMessage(
                            "§c§l[ANTICHEAT] §e" + player.getName() +
                            " §7tiene §c" + count + "§7 violaciones. Tipo: §c" + type));
        }
    }

    private void kickPlayer(Player player, String reason) {
        String arenaName = plugin.getGameManager().getPlayerArena(player.getUniqueId());
        if (arenaName != null) plugin.getGameManager().leaveArena(player);

        Bukkit.getScheduler().runTask(plugin, () ->
            player.kickPlayer("§6§lAdvancedPaintBattle\n\n§cExpulsado por comportamiento sospechoso.\n§7Tipo: §c" + reason));

        plugin.getLogger().warning("[APB-ANTICHEAT] " + player.getName() +
                " expulsado. Tipo: " + reason);

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("advancedpaintbattle.admin"))
                .forEach(admin -> admin.sendMessage(
                        "§c§l[ANTICHEAT] §e" + player.getName() +
                        " §cfue expulsado por: §f" + reason));

        plugin.getAdminLogger().log("ANTICHEAT expulso a " + player.getName() + " por: " + reason);
    }

    public void clearViolations(UUID uuid) { violations.remove(uuid); }
    public int getViolations(UUID uuid) { return violations.getOrDefault(uuid, 0); }
}
