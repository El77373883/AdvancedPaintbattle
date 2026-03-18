package me.soyadrianyt001.advancedpaintbattle.models;

import org.bukkit.Location;
import java.util.*;

public class GameSession {

    private final String arenaName;
    private final List<GamePlayer> players;
    private final List<GamePlayer> spectators;
    private String currentTheme;
    private String gameMode;
    private int currentRound;
    private int maxRounds;
    private int timeLeft;
    private GameState state;
    private Map<UUID, Location> canvasLocations;
    private Map<UUID, Integer> roundScores;
    private List<String> chatLog;

    public enum GameState {
        WAITING, COUNTDOWN, PAINTING, VOTING, ROUND_END, PODIUM
    }

    public GameSession(String arenaName) {
        this.arenaName = arenaName;
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.currentRound = 0;
        this.maxRounds = 3;
        this.state = GameState.WAITING;
        this.canvasLocations = new HashMap<>();
        this.roundScores = new HashMap<>();
        this.chatLog = new ArrayList<>();
        this.gameMode = "NORMAL";
    }

    public String getArenaName() { return arenaName; }
    public List<GamePlayer> getPlayers() { return players; }
    public List<GamePlayer> getSpectators() { return spectators; }
    public String getCurrentTheme() { return currentTheme; }
    public void setCurrentTheme(String theme) { this.currentTheme = theme; }
    public String getGameMode() { return gameMode; }
    public void setGameMode(String mode) { this.gameMode = mode; }
    public int getCurrentRound() { return currentRound; }
    public void nextRound() { this.currentRound++; }
    public int getMaxRounds() { return maxRounds; }
    public int getTimeLeft() { return timeLeft; }
    public void setTimeLeft(int time) { this.timeLeft = time; }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public Map<UUID, Location> getCanvasLocations() { return canvasLocations; }
    public Map<UUID, Integer> getRoundScores() { return roundScores; }
    public List<String> getChatLog() { return chatLog; }
    public boolean isLastRound() { return currentRound >= maxRounds; }

    public GamePlayer getGamePlayer(UUID uuid) {
        return players.stream().filter(p -> p.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public List<GamePlayer> getTopPlayers() {
        List<GamePlayer> sorted = new ArrayList<>(players);
        sorted.sort((a, b) -> b.getPoints() - a.getPoints());
        return sorted;
    }
}
