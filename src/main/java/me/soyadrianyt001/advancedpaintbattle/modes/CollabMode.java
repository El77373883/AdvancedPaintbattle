package me.soyadrianyt001.advancedpaintbattle.modes;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.managers.CanvasManager;
import me.soyadrianyt001.advancedpaintbattle.models.Arena;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import me.soyadrianyt001.advancedpaintbattle.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CollabMode extends GameMode {

    private Location sharedCanvas;

    public CollabMode(AdvancedPaintBattle plugin, GameSession session) {
        super(plugin, session);
    }

    @Override
    public void onStart() {
        // Un solo lienzo grande para todos
        Arena arena = plugin.getArenaManager().getArena(session.getArenaName());
        if (arena != null && arena.getCanvasOrigin() != null) {
            sharedCanvas = arena.getCanvasOrigin().clone();
            buildSharedCanvas();
        }

        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null) return;
            p.sendTitle("§a§l¡MODO COLABORATIVO!", "§eTodos pintan juntos", 10, 60, 10);
            p.sendMessage("§a§l★ §e¡Todos pintan en el mismo lienzo!");
            p.sendMessage("§a§l★ §eTema: §f§l" + session.getCurrentTheme());
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            // Teleportar cerca del lienzo compartido
            if (sharedCanvas != null) {
                p.teleport(sharedCanvas.clone().add(
                        plugin.getConfigManager().getCanvasSize() / 2.0, 1,
                        plugin.getConfigManager().getCanvasSize() / 2.0));
            }
        });
    }

    private void buildSharedCanvas() {
        if (sharedCanvas == null) return;
        int size = plugin.getConfigManager().getCanvasSize() * 2; // Lienzo doble
        Material floor = Material.getMaterial(plugin.getConfigManager().getCanvasFloor());
        Material border = Material.getMaterial(plugin.getConfigManager().getCanvasBorder());
        if (floor == null) floor = Material.WHITE_CONCRETE;
        if (border == null) border = Material.GRAY_CONCRETE;

        for (int x = -1; x <= size; x++) {
            for (int z = -1; z <= size; z++) {
                Location loc = sharedCanvas.clone().add(x, 0, z);
                if (x == -1 || x == size || z == -1 || z == size) {
                    loc.getBlock().setType(border);
                } else {
                    loc.getBlock().setType(floor);
                }
            }
        }
        // Guardar canvas para todos los jugadores
        session.getPlayers().forEach(gp ->
                session.getCanvasLocations().put(gp.getUuid(), sharedCanvas));
    }

    @Override
    public void onTick(int timeLeft) {
        if (timeLeft == 30) {
            ChatUtil.sendArenaMessage(plugin, session.getArenaName(),
                    "§a§l★ §e¡30 segundos! ¡Terminen el dibujo juntos!");
        }
    }

    @Override
    public void onEnd() {
        sharedCanvas = null;
    }

    @Override
    public String getName() { return "COLLAB"; }

    @Override
    public String getDisplayName() { return "§a§lModo Colaborativo"; }

    @Override
    public String getDescription() { return "§7Todos pintan en un lienzo gigante"; }
}
