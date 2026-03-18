package me.soyadrianyt001.advancedpaintbattle.modes;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import me.soyadrianyt001.advancedpaintbattle.utils.ChatUtil;
import me.soyadrianyt001.advancedpaintbattle.utils.ThemeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class RapidfireMode extends GameMode {

    private int totalThemes = 10;
    private int currentThemeIndex = 0;
    private int timePerTheme = 15;
    private int lastThemeTime = 0;

    public RapidfireMode(AdvancedPaintBattle plugin, GameSession session) {
        super(plugin, session);
    }

    @Override
    public void onStart() {
        currentThemeIndex = 0;
        lastThemeTime = totalThemes * timePerTheme;
        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null) return;
            p.sendTitle("§e§l¡RAPIDFIRE!", "§f10 temas - §e15s cada uno", 10, 60, 10);
            p.sendMessage("§e§l⚡ §f¡10 temas, 15 segundos cada uno!");
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.5f);
        });
    }

    @Override
    public void onTick(int timeLeft) {
        int elapsed = lastThemeTime - timeLeft;
        if (elapsed >= timePerTheme && currentThemeIndex < totalThemes) {
            currentThemeIndex++;
            lastThemeTime = timeLeft;
            nextTheme();
        }

        // Advertencia de tiempo
        int timeInCurrentTheme = timePerTheme - (lastThemeTime - timeLeft);
        if (timeInCurrentTheme == 5) {
            session.getPlayers().forEach(gp -> {
                Player p = Bukkit.getPlayer(gp.getUuid());
                if (p != null) {
                    p.sendTitle("§c§l5", "§esegundos!", 2, 15, 3);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                }
            });
        }
    }

    private void nextTheme() {
        if (currentThemeIndex > totalThemes) return;
        String newTheme = ThemeUtil.selectTheme(plugin, session);
        session.setCurrentTheme(newTheme);

        ChatUtil.broadcastTitle(plugin, session.getArenaName(),
                "§e§l" + currentThemeIndex + "/" + totalThemes,
                "§f§l" + newTheme,
                3, 30, 3);

        ChatUtil.sendArenaMessage(plugin, session.getArenaName(),
                "§e§l⚡ §7Tema §e" + currentThemeIndex + "§7/§e" + totalThemes + "§7: §f§l" + newTheme);

        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f);
            }
        });
    }

    @Override
    public void onEnd() {
        currentThemeIndex = 0;
    }

    @Override
    public String getName() { return "RAPIDFIRE"; }

    @Override
    public String getDisplayName() { return "§e§lModo Rapidfire"; }

    @Override
    public String getDescription() { return "§710 temas, 15 segundos cada uno"; }
}
