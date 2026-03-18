package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FriendManager {

    private final AdvancedPaintBattle plugin;
    private File file;
    private FileConfiguration config;
    private Map<UUID, UUID> pendingRequests = new HashMap<>();

    public FriendManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "friends.yml");
        if (!file.exists()) try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void sendRequest(Player sender, Player target) {
        pendingRequests.put(target.getUniqueId(), sender.getUniqueId());
        target.sendMessage("§6§l[APB] §e" + sender.getName() + " §7te envio solicitud de amistad. §a/apb friend accept " + sender.getName());
    }

    public void acceptRequest(Player player, Player sender) {
        if (!pendingRequests.containsKey(player.getUniqueId())) return;
        addFriend(player.getUniqueId(), sender.getUniqueId());
        addFriend(sender.getUniqueId(), player.getUniqueId());
        pendingRequests.remove(player.getUniqueId());
        player.sendMessage("§a¡Ahora eres amigo de " + sender.getName() + "!");
        sender.sendMessage("§a" + player.getName() + " acepto tu solicitud de amistad!");
    }

    private void addFriend(UUID a, UUID b) {
        List<String> friends = config.getStringList(a.toString() + ".friends");
        if (!friends.contains(b.toString())) friends.add(b.toString());
        config.set(a.toString() + ".friends", friends);
        save();
    }

    public List<String> getFriends(UUID uuid) {
        return config.getStringList(uuid.toString() + ".friends");
    }

    private void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
