package me.soyadrianyt001.advancedpaintbattle;

import me.soyadrianyt001.advancedpaintbattle.commands.APBCommand;
import me.soyadrianyt001.advancedpaintbattle.listeners.*;
import me.soyadrianyt001.advancedpaintbattle.managers.*;
import me.soyadrianyt001.advancedpaintbattle.modes.ModeManager;
import me.soyadrianyt001.advancedpaintbattle.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedPaintBattle extends JavaPlugin {

    private static AdvancedPaintBattle instance;

    // MANAGERS
    private ConfigManager configManager;
    private DataManager dataManager;
    private PlayerDataManager playerDataManager;
    private DatabaseManager databaseManager;
    private MySQLManager mySQLManager;
    private MessageManager messageManager;
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
    private AnnouncementManager announcementManager;
    private EventManager eventManager;
    private DiscordManager discordManager;
    private ModeManager modeManager;
    private AdminLogger adminLogger;
    private ReportManager reportManager;

    // LISTENERS
    private AntiCheatListener antiCheatListener;

    // UTILS
    private FileUtil fileUtil;
    private UpdateChecker updateChecker;
    private BungeeCordHook bungeeCordHook;

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

        getLogger().info("[APB] AdvancedPaintBattle habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) gameManager.stopAllGames();
        if (dataManager != null) dataManager.saveAll();
        if (databaseManager != null) databaseManager.close();
        if (mySQLManager != null) mySQLManager.close();
        getLogger().info("[APB] AdvancedPaintBattle deshabilitado. Datos guardados.");
    }

    private void printBanner() {
        Bukkit.getConsoleSender().sendMessage("§6§l╔══════════════════════════════════════╗");
        Bukkit.getConsoleSender().sendMessage("§6§l║  §e§lAdvancedPaintBattle §6§lv1.0.0        §6§l║");
        Bukkit.getConsoleSender().sendMessage("§6§l║  §7Creado por §bsoyadrianyt001           §6§l║");
        Bukkit.getConsoleSender().sendMessage("§6§l║  §7El plugin de pintura mas avanzado    §6§l║");
        Bukkit.getConsoleSender().sendMessage("§6§l╚══════════════════════════════════════╝");
    }

    private void loadManagers() {
        this.fileUtil = new FileUtil(this);
        this.configManager = new ConfigManager(this);
        this.adminLogger = new AdminLogger(this);
        this.reportManager = new ReportManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.mySQLManager = new MySQLManager(this);
        this.messageManager = new MessageManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.dataManager = new DataManager(this);
        this.statsManager = new StatsManager(this);
        this.coinManager = new CoinManager(this);
        this.rankManager = new RankManager(this);
        this.achievementManager = new AchievementManager(this);
        this.arenaManager = new ArenaManager(this);
        this.modeManager = new ModeManager(this);
        this.gameManager = new GameManager(this);
        this.tournamentManager = new TournamentManager(this);
        this.missionManager = new MissionManager(this);
        this.battlePassManager = new BattlePassManager(this);
        this.galleryManager = new GalleryManager(this);
        this.friendManager = new FriendManager(this);
        this.announcementManager = new AnnouncementManager(this);
        this.eventManager = new EventManager(this);
        this.discordManager = new DiscordManager(this);
    }

    private void registerListeners() {
        this.antiCheatListener = new AntiCheatListener(this);
        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PaintListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(antiCheatListener, this);
        new ChatUtil(this);
    }

    private void registerCommands() {
        APBCommand cmd = new APBCommand(this);
        getCommand("apb").setExecutor(cmd);
        getCommand("apb").setTabCompleter(cmd);
    }

    private void setupSoftDepends() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHook(this).register();
            getLogger().info("[APB] PlaceholderAPI conectado.");
        }
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            VaultHook.setup();
            getLogger().info("[APB] Vault conectado.");
        }
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            WorldGuardHook.setup();
            getLogger().info("[APB] WorldGuard conectado.");
        }
        if (getConfig().getBoolean("bungeecord.enabled", false)) {
            this.bungeeCordHook = new BungeeCordHook(this);
            getLogger().info("[APB] BungeeCord conectado.");
        }
    }

    // ─── GETTERS ───────────────────────────────────────────────
    public static AdvancedPaintBattle getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public DataManager getDataManager() { return dataManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public MySQLManager getMySQLManager() { return mySQLManager; }
    public MessageManager getMessageManager() { return messageManager; }
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
    public AnnouncementManager getAnnouncementManager() { return announcementManager; }
    public EventManager getEventManager() { return eventManager; }
    public DiscordManager getDiscordManager() { return discordManager; }
    public ModeManager getModeManager() { return modeManager; }
    public AdminLogger getAdminLogger() { return adminLogger; }
    public ReportManager getReportManager() { return reportManager; }
    public AntiCheatListener getAntiCheatListener() { return antiCheatListener; }
    public FileUtil getFileUtil() { return fileUtil; }
    public UpdateChecker getUpdateChecker() { return updateChecker; }
    public BungeeCordHook getBungeeCordHook() { return bungeeCordHook; }
}
