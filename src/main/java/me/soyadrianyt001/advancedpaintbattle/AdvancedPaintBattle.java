package me.soyadrianyt001.advancedpaintbattle;

import me.soyadrianyt001.advancedpaintbattle.commands.APBCommand;
import me.soyadrianyt001.advancedpaintbattle.listeners.*;
import me.soyadrianyt001.advancedpaintbattle.managers.*;
import me.soyadrianyt001.advancedpaintbattle.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedPaintBattle extends JavaPlugin {

    private static AdvancedPaintBattle instance;
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private StatsManager statsManager;
    private CoinManager coinManager;
    private RankManager rankManager;
    private AchievementManager achievementManager;
    private TournamentManager tournamentManager;
    private MissionManager missionManager;
    private BattlePassManager battlePassManager;
    private GalleryManager galleryManager;
    private FriendManager friendManager;
    private DatabaseManager databaseManager;
    private MessageManager messageManager;
    private UpdateChecker updateChecker;
    private AnnouncementManager announcementManager;
    private EventManager eventManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        printBanner();

        loadManagers();
        registerListeners();
        registerCommands();
        setupSoftDepends();

        updateChecker = new UpdateChecker(this);
        updateChecker.check();

        announcementManager.startAnnouncing();
        eventManager.checkSpecialEvents();

        getLogger().info("AdvancedPaintBattle habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) gameManager.stopAllGames();
        if (statsManager != null) statsManager.saveAll();
        if (databaseManager != null) databaseManager.close();
        getLogger().info("AdvancedPaintBattle deshabilitado.");
    }

    private void printBanner() {
        Bukkit.getConsoleSender().sendMessage("§6§l╔══════════════════════════════════════╗");
        Bukkit.getConsoleSender().sendMessage("§6§l║  §e§lAdvancedPaintBattle §6§lv1.0.0        §6§l║");
        Bukkit.getConsoleSender().sendMessage("§6§l║  §7Creado por §bsoyadrianyt001           §6§l║");
        Bukkit.getConsoleSender().sendMessage("§6§l║  §7El plugin de pintura mas avanzado    §6§l║");
        Bukkit.getConsoleSender().sendMessage("§6§l╚══════════════════════════════════════╝");
    }

    private void loadManagers() {
        this.databaseManager = new DatabaseManager(this);
        this.messageManager = new MessageManager(this);
        this.statsManager = new StatsManager(this);
        this.coinManager = new CoinManager(this);
        this.rankManager = new RankManager(this);
        this.achievementManager = new AchievementManager(this);
        this.arenaManager = new ArenaManager(this);
        this.gameManager = new GameManager(this);
        this.tournamentManager = new TournamentManager(this);
        this.missionManager = new MissionManager(this);
        this.battlePassManager = new BattlePassManager(this);
        this.galleryManager = new GalleryManager(this);
        this.friendManager = new FriendManager(this);
        this.announcementManager = new AnnouncementManager(this);
        this.eventManager = new EventManager(this);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PaintListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AntiCheatListener(this), this);
    }

    private void registerCommands() {
        getCommand("apb").setExecutor(new APBCommand(this));
        getCommand("apb").setTabCompleter(new APBCommand(this));
    }

    private void setupSoftDepends() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHook(this).register();
            getLogger().info("PlaceholderAPI conectado.");
        }
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            VaultHook.setup();
            getLogger().info("Vault conectado.");
        }
    }

    public static AdvancedPaintBattle getInstance() { return instance; }
    public ArenaManager getArenaManager() { return arenaManager; }
    public GameManager getGameManager() { return gameManager; }
    public StatsManager getStatsManager() { return statsManager; }
    public CoinManager getCoinManager() { return coinManager; }
    public RankManager getRankManager() { return rankManager; }
    public AchievementManager getAchievementManager() { return achievementManager; }
    public TournamentManager getTournamentManager() { return tournamentManager; }
    public MissionManager getMissionManager() { return missionManager; }
    public BattlePassManager getBattlePassManager() { return battlePassManager; }
    public GalleryManager getGalleryManager() { return galleryManager; }
    public FriendManager getFriendManager() { return friendManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public MessageManager getMessageManager() { return messageManager; }
    public AnnouncementManager getAnnouncementManager() { return announcementManager; }
    public EventManager getEventManager() { return eventManager; }
}
