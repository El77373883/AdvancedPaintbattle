package me.soyadrianyt001.advancedpaintbattle.modes;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import me.soyadrianyt001.advancedpaintbattle.utils.ChatUtil;
import me.soyadrianyt001.advancedpaintbattle.utils.ThemeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ChaosMode extends GameMode {

    private int themeChangeInterval = 20; // segundos
    private int lastChange = 0;

    public ChaosMode(AdvancedPaintBattle plugin, GameSession session) {
        super(plugin, session);
    }

    @Override
    public void onStart() {
        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null) return;
            p.sendTitle("§5§l¡MODO CAOS!", "§eTema cambia cada 20s", 10, 60, 10);
            p.sendMessage("§5§l⚠ §eEl tema cambiara cada §520 §esegundos!");
            p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.3f, 1.5f);
        });
        lastChange = plugin.getConfigManager().getPaintTime();
    }

    @Override
    public void onTick(int timeLeft) {
        int elapsed = lastChange - timeLeft;
        if (elapsed >= themeChangeInterval) {
            lastChange = timeLeft;
            changeTheme();
        }
    }

    private void changeTheme() {
        String newTheme = ThemeUtil.selectTheme(plugin, session);
        session.setCurrentTheme(newTheme);

        ChatUtil.broadcastTitle(plugin, session.getArenaName(),
                "§5§l⚡ TEMA CAMBIADO ⚡",
                "§e§l" + newTheme,
                5, 40, 5);

        ChatUtil.sendArenaMessage(plugin, session.getArenaName(),
                "§5§l⚡ §eNuevo tema: §f§l" + newTheme);

        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null) {
                p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5f, 1.5f);
            }
        });
    }

    @Override
    public void onEnd() { lastChange = 0; }

    @Override
    public String getName() { return "CHAOS"; }

    @Override
    public String getDisplayName() { return "§5§lModo Caos"; }

    @Override
    public String getDescription() { return "§7El tema cambia cada 20 segundos"; }
}
