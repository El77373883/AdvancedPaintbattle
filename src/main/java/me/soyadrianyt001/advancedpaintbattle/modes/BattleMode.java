package me.soyadrianyt001.advancedpaintbattle.modes;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.managers.CanvasManager;
import me.soyadrianyt001.advancedpaintbattle.models.GamePlayer;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;
import me.soyadrianyt001.advancedpaintbattle.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BattleMode extends GameMode {

    private boolean sabotageEnabled = false;
    private int sabotageStartTime = 30; // sabotaje activo en los ultimos 30s

    public BattleMode(AdvancedPaintBattle plugin, GameSession session) {
        super(plugin, session);
    }

    @Override
    public void onStart() {
        session.getPlayers().forEach(gp -> {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null) return;
            p.sendTitle("§c§l¡MODO BATALLA!", "§ePuedes destruir el lienzo rival", 10, 60, 10);
            p.sendMessage("§c§l⚔ §e¡En los ultimos 30s podras §cbombardar §eel lienzo enemigo!");
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5f, 1f);
        });
    }

    @Override
    public void onTick(int timeLeft) {
        if (timeLeft <= sabotageStartTime && !sabotageEnabled) {
            sabotageEnabled = true;
            ChatUtil.broadcastTitle(plugin, session.getArenaName(),
                    "§c§l⚔ ¡SABOTAJE!",
                    "§e¡Destruye el lienzo enemigo!",
                    10, 60, 10);
            ChatUtil.sendArenaMessage(plugin, session.getArenaName(),
                    "§c§l⚔ §e¡El sabotaje esta activo! Puedes usar el borrador en lienzos rivales!");
            session.getPlayers().forEach(gp -> {
                Player p = Bukkit.getPlayer(gp.getUuid());
                if (p != null) {
                    p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 1f);
                }
            });
        }
    }

    public boolean isSabotageEnabled() { return sabotageEnabled; }

    public boolean isEnemyCanvas(GamePlayer attacker, GamePlayer target) {
        return !attacker.getUuid().equals(target.getUuid());
    }

    public void onSabotage(Player attacker, Player target) {
        attacker.sendMessage("§c§l⚔ §e¡Borraste un bloque del lienzo de §c" + target.getName() + "§e!");
        target.sendMessage("§c§l⚔ §c" + attacker.getName() + " §eborro un bloque de tu lienzo!");
        attacker.playSound(attacker.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);
    }

    @Override
    public void onEnd() {
        sabotageEnabled = false;
    }

    @Override
    public String getName() { return "BATTLE"; }

    @Override
    public String getDisplayName() { return "§c§lModo Batalla"; }

    @Override
    public String getDescription() { return "§7Puedes destruir el lienzo rival"; }
}
