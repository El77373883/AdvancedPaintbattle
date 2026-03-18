package me.soyadrianyt001.advancedpaintbattle.models;

import org.bukkit.entity.Player;
import java.util.*;

public class GamePlayer {

    private final UUID uuid;
    private final String name;
    private int points;
    private int votes;
    private int coins;
    private int totalPoints;
    private int gamesPlayed;
    private int gamesWon;
    private String rank;
    private boolean hasVoted;
    private UUID votedFor;
    private PlayerState state;
    private String selectedBrush;
    private Stack<Map<String, Object>> undoStack;
    private Stack<Map<String, Object>> redoStack;

    public enum PlayerState {
        LOBBY, PAINTING, VOTING, SPECTATING, PODIUM
    }

    public GamePlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.points = 0;
        this.votes = 0;
        this.hasVoted = false;
        this.state = PlayerState.LOBBY;
        this.selectedBrush = "NORMAL";
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.rank = "Aprendiz";
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public int getPoints() { return points; }
    public void addPoints(int points) { this.points += points; }
    public int getVotes() { return votes; }
    public void addVote() { this.votes++; }
    public int getCoins() { return coins; }
    public void addCoins(int coins) { this.coins += coins; }
    public boolean hasVoted() { return hasVoted; }
    public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }
    public UUID getVotedFor() { return votedFor; }
    public void setVotedFor(UUID votedFor) { this.votedFor = votedFor; }
    public PlayerState getState() { return state; }
    public void setState(PlayerState state) { this.state = state; }
    public String getSelectedBrush() { return selectedBrush; }
    public void setSelectedBrush(String brush) { this.selectedBrush = brush; }
    public Stack<Map<String, Object>> getUndoStack() { return undoStack; }
    public Stack<Map<String, Object>> getRedoStack() { return redoStack; }
    public int getTotalPoints() { return totalPoints; }
    public void addTotalPoints(int p) { this.totalPoints += p; }
    public int getGamesPlayed() { return gamesPlayed; }
    public void incrementGamesPlayed() { this.gamesPlayed++; }
    public int getGamesWon() { return gamesWon; }
    public void incrementGamesWon() { this.gamesWon++; }
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
}
