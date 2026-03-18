package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final AdvancedPaintBattle plugin;
    private FileConfiguration config;

    public ConfigManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    // TIEMPOS
    public int getLobbyCountdown() { return config.getInt("lobby-countdown", 30); }
    public int getPaintTime() { return config.getInt("paint-time", 90); }
    public int getVoteTime() { return config.getInt("vote-time", 25); }
    public int getPodiumTime() { return config.getInt("podium-time", 10); }
    public int getAnnounceInterval() { return config.getInt("announcements.interval", 300); }
    public int getAutoSaveInterval() { return config.getInt("stats.save-interval", 300); }

    // JUGADORES
    public int getMinPlayers() { return config.getInt("min-players", 2); }
    public int getMaxPlayers() { return config.getInt("max-players", 8); }
    public int getRounds() { return config.getInt("rounds", 3); }

    // LIENZO
    public int getCanvasSize() { return config.getInt("canvas-size", 16); }
    public int getCanvasSpacing() { return config.getInt("canvas-spacing", 6); }
    public String getCanvasFloor() { return config.getString("canvas-floor", "WHITE_CONCRETE"); }
    public String getCanvasBorder() { return config.getString("canvas-border", "GRAY_CONCRETE"); }

    // PUNTOS
    public int getPointsFirst() { return config.getInt("points-first", 300); }
    public int getPointsSecond() { return config.getInt("points-second", 200); }
    public int getPointsThird() { return config.getInt("points-third", 100); }
    public int getPointsVoteReceived() { return config.getInt("points-vote-received", 50); }
    public int getPointsVoted() { return config.getInt("points-voted", 10); }

    // MONEDAS
    public int getCoinsWin() { return config.getInt("coins-win", 100); }
    public int getCoinsSecond() { return config.getInt("coins-second", 60); }
    public int getCoinsThird() { return config.getInt("coins-third", 30); }
    public int getCoinsParticipate() { return config.getInt("coins-participate", 10); }
    public int getCoinsVote() { return config.getInt("coins-vote", 5); }

    // EFECTOS
    public boolean isFireworksEnabled() { return config.getBoolean("effects.fireworks", true); }
    public boolean isParticlesEnabled() { return config.getBoolean("effects.particles", true); }
    public boolean isSoundsEnabled() { return config.getBoolean("effects.sounds", true); }
    public boolean isTitlesEnabled() { return config.getBoolean("effects.titles", true); }

    // INTEGRACIONES
    public boolean isMySQLEnabled() { return config.getBoolean("mysql.enabled", false); }
    public boolean isDiscordEnabled() { return config.getBoolean("discord.enabled", false); }
    public boolean isVaultEnabled() { return config.getBoolean("vault.enabled", false); }
    public boolean isWorldGuardEnabled() { return config.getBoolean("worldguard.enabled", false); }
    public boolean isBungeeCordEnabled() { return config.getBoolean("bungeecord.enabled", false); }

    // UPDATE CHECKER
    public boolean isUpdateCheckerEnabled() { return config.getBoolean("update-checker.enabled", true); }
    public boolean isNotifyAdmins() { return config.getBoolean("update-checker.notify-admins", true); }

    // ANUNCIOS
    public boolean isAnnouncementsEnabled() { return config.getBoolean("announcements.enabled", true); }

    // BACKUP
    public boolean isBackupEnabled() { return config.getBoolean("backup.enabled", true); }
    public int getBackupInterval() { return config.getInt("backup.interval", 3600); }
}
