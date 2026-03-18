package me.soyadrianyt001.advancedpaintbattle.modes;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ModeManager {

    private final AdvancedPaintBattle plugin;
    private final Map<String, GameMode> activeModes = new HashMap<>();

    public ModeManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    public GameMode createMode(String modeName, GameSession session) {
        return switch (modeName.toUpperCase()) {
            case "BLIND" -> new BlindMode(plugin, session);
            case "CHAOS" -> new ChaosMode(plugin, session);
            case "RAPIDFIRE" -> new RapidfireMode(plugin, session);
            case "COLLAB" -> new CollabMode(plugin, session);
            case "BATTLE" -> new BattleMode(plugin, session);
            case "TEAM" -> new TeamMode(plugin, session);
            default -> null;
        };
    }

    public GameMode getRandomMode() {
        String[] modes = {"NORMAL", "NORMAL", "NORMAL", "BLIND", "CHAOS", "RAPIDFIRE", "COLLAB", "BATTLE", "TEAM"};
        String selected = modes[new Random().nextInt(modes.length)];
        return selected.equals("NORMAL") ? null : createMode(selected, null);
    }

    public void setActiveMode(String arenaName, GameMode mode) {
        if (mode == null) activeModes.remove(arenaName);
        else activeModes.put(arenaName, mode);
    }

    public GameMode getActiveMode(String arenaName) {
        return activeModes.get(arenaName);
    }

    public void removeMode(String arenaName) {
        activeModes.remove(arenaName);
    }

    public boolean hasActiveMode(String arenaName) {
        return activeModes.containsKey(arenaName);
    }
}
