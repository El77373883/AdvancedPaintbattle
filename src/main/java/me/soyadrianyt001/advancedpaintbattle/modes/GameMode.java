package me.soyadrianyt001.advancedpaintbattle.modes;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;

public abstract class GameMode {

    protected final AdvancedPaintBattle plugin;
    protected final GameSession session;

    public GameMode(AdvancedPaintBattle plugin, GameSession session) {
        this.plugin = plugin;
        this.session = session;
    }

    public abstract void onStart();
    public abstract void onTick(int timeLeft);
    public abstract void onEnd();
    public abstract String getName();
    public abstract String getDisplayName();
    public abstract String getDescription();
}
