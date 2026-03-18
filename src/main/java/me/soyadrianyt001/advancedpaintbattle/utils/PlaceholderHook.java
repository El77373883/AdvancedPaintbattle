package me.soyadrianyt001.advancedpaintbattle.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderHook extends PlaceholderExpansion {

    private final AdvancedPaintBattle plugin;

    public PlaceholderHook(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    @Override public @NotNull String getIdentifier() { return "apb"; }
    @Override public @NotNull String getAuthor() { return "soyadrianyt001"; }
    @Override public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        switch (params) {
            case "wins": return String.valueOf(plugin.getStatsManager().getWins(player.getUniqueId()));
            case "games": return String.valueOf(plugin.getStatsManager().getGamesPlayed(player.getUniqueId()));
            case "points": return String.valueOf(plugin.getStatsManager().getTotalPoints(player.getUniqueId()));
            case "coins": return String.valueOf(plugin.getCoinManager().getCoins(player.getUniqueId()));
            case "rank": return plugin.getRankManager().getRank(player.getUniqueId());
            case "in_game": return String.valueOf(plugin.getGameManager().isInGame(player.getUniqueId()));
            default: return null;
        }
    }
}
