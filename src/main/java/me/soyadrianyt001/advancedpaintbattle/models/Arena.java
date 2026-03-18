package me.soyadrianyt001.advancedpaintbattle.models;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

public class Arena {

    private final String name;
    private Location lobby;
    private Location canvasOrigin;
    private ArenaState state;
    private int maxPlayers;
    private int minPlayers;
    private List<String> players;

    public enum ArenaState {
        WAITING, COUNTDOWN, PAINTING, VOTING, PODIUM, DISABLED
    }

    public Arena(String name) {
        this.name = name;
        this.state = ArenaState.WAITING;
        this.players = new ArrayList<>();
        this.maxPlayers = 8;
        this.minPlayers = 2;
    }

    public String getName() { return name; }
    public Location getLobby() { return lobby; }
    public void setLobby(Location lobby) { this.lobby = lobby; }
    public Location getCanvasOrigin() { return canvasOrigin; }
    public void setCanvasOrigin(Location canvasOrigin) { this.canvasOrigin = canvasOrigin; }
    public ArenaState getState() { return state; }
    public void setState(ArenaState state) { this.state = state; }
    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
    public int getMinPlayers() { return minPlayers; }
    public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers; }
    public List<String> getPlayers() { return players; }
    public boolean isFull() { return players.size() >= maxPlayers; }
    public boolean isReady() { return state == ArenaState.WAITING && lobby != null && canvasOrigin != null; }
}
