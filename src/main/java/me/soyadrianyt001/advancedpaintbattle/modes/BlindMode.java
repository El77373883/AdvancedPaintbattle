package me.soyadrianyt001.advancedpaintbattle.modes;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import me.soyadrianyt001.advancedpaintbattle.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BlindMode extends GameMode {

    private boolean themeRevealed = false;

    public BlindMode(AdvancedPaintBattle plugin, GameSession session) {
        super(plugin, session);
    }

    @Override
    public void onStart() {
        // Al inicio no se revela el tema
        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null) return;
            p.sendTitle("§c§l¡MODO CIEGO!", "§eTema oculto hasta la mitad", 10, 60, 10);
            p.sendMessage("§c§l⚠ §eEl tema se revelara a la mitad del tiempo!");
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5f, 1.5f);
        });
    }

    @Override
    public void onTick(int timeLeft) {
        int paintTime = plugin.getConfigManager().getPaintTime();
        int halfTime = paintTime / 2;

        // Revelar tema a la mitad
        if (timeLeft <= halfTime && !themeRevealed) {
            themeRevealed = true;
            ChatUtil.broadcastTitle(plugin, session.getArenaName(),
                    "§6§l✦ TEMA REVELADO ✦",
                    "§e§l" + session.getCurrentTheme(),
                    10, 80, 10);
            ChatUtil.sendArenaMessage(plugin, session.getArenaName(),
                    "§6§l✦ §eTema: §f§l" + session.getCurrentTheme() + " §6§l✦");
            session.getPlayers().forEach(gp -> {
                Player p = Bukkit.getPlayer(gp.getUuid());
                if (p != null) p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.5f);
            });
        }

        // Mensajes de progreso
        if (timeLeft == halfTime + 10) {
            ChatUtil.sendArenaMessage(plugin, session.getArenaName(),
                    "§e§l⚠ ¡El tema se revela en 10 segundos!");
        }
    }

    @Override
    public void onEnd() {
        themeRevealed = false;
    }

    @Override
    public String getName() { return "BLIND"; }

    @Override
    public String getDisplayName() { return "§c§lModo Ciego"; }

    @Override
    public String getDescription() { return "§7El tema se revela a la mitad del tiempo"; }
}
