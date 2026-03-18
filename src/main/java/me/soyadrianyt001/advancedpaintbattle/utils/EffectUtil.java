package me.soyadrianyt001.advancedpaintbattle.utils;

import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class EffectUtil {

    public static void playCountdownSound(Player player, int time) {
        float pitch = time <= 1 ? 2.0f : 1.0f + (5 - time) * 0.2f;
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, pitch);
    }

    public static void spawnCountdownParticles(Player player) {
        Location loc = player.getLocation().add(0, 1, 0);
        player.getWorld().spawnParticle(Particle.FIREWORK, loc, 30, 0.5, 0.5, 0.5, 0.1);
    }

    public static void spawnVoteParticles(Player player) {
        Location loc = player.getLocation().add(0, 1, 0);
        player.getWorld().spawnParticle(Particle.HEART, loc, 10, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 20, 0.5, 1, 0.5, 0);
    }

    public static void spawnStarParticles(Player player) {
        Location loc = player.getLocation().add(0, 2, 0);
        player.getWorld().spawnParticle(Particle.FIREWORK, loc, 50, 1, 1, 1, 0.2);
        player.getWorld().spawnParticle(Particle.GLOW, loc, 30, 0.5, 1, 0.5, 0);
    }

    public static void spawnWinFireworks(Player player) {
        for (int i = 0; i < 5; i++) {
            final int fi = i;
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("AdvancedPaintBattle"), () -> {
                Firework fw = player.getWorld().spawn(player.getLocation().add(Math.random() * 4 - 2, 0, Math.random() * 4 - 2), Firework.class);
                FireworkMeta meta = fw.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder()
                        .withColor(Color.YELLOW, Color.GOLD, Color.ORANGE)
                        .withFade(Color.WHITE)
                        .with(FireworkEffect.Type.STAR)
                        .trail(true).flicker(true).build());
                meta.setPower(1);
                fw.setFireworkMeta(meta);
            }, fi * 10L);
        }
    }

    public static void spawnWinParticles(Player player) {
        Location loc = player.getLocation().add(0, 1, 0);
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc, 100, 1, 2, 1, 0.3);
        player.getWorld().spawnParticle(Particle.FIREWORK, loc, 100, 1, 1, 1, 0.5);
    }

    public static void spawnRankUpParticles(Player player) {
        Location loc = player.getLocation().add(0, 1, 0);
        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 60, 0.5, 1, 0.5, 0.1);
        player.getWorld().spawnParticle(Particle.FIREWORK, loc, 60, 0.5, 1, 0.5, 0.2);
    }

    public static void spawnAchievementParticles(Player player) {
        Location loc = player.getLocation().add(0, 1, 0);
        player.getWorld().spawnParticle(Particle.WITCH, loc, 50, 0.5, 1, 0.5, 0);
        player.getWorld().spawnParticle(Particle.GLOW, loc, 50, 0.5, 1, 0.5, 0);
    }

    public static void spawnPaintParticles(Player player, Color color) {
        Location loc = player.getLocation().add(0, 1.5, 0);
        player.getWorld().spawnParticle(Particle.DUST, loc, 10,
                0.2, 0.2, 0.2, new Particle.DustOptions(color, 1.5f));
    }

    public static void spawnCreatorParticles(Player player) {
        Location loc = player.getLocation().add(0, 1, 0);
        player.getWorld().spawnParticle(Particle.HEART, loc, 20, 1, 1, 1, 0);
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc, 40, 1, 2, 1, 0.2);
    }
}
