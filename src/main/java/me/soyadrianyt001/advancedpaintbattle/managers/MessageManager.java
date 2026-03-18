package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class MessageManager {

    private final AdvancedPaintBattle plugin;
    private FileConfiguration messages;

    public MessageManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    private void loadMessages() {
        String lang = plugin.getConfig().getString("language", "es");
        File file = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
        if (!file.exists()) plugin.saveResource("messages_" + lang + ".yml", false);
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String key) {
        String msg = messages.getString(key, "&cMensaje no encontrado: " + key);
        return colorize(msg);
    }

    public String get(String key, String... replacements) {
        String msg = get(key);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }
        return msg;
    }

    public void send(Player player, String key, String... replacements) {
        String prefix = get("prefix");
        player.sendMessage(prefix + get(key, replacements));
    }

    public void sendRaw(Player player, String key, String... replacements) {
        player.sendMessage(get(key, replacements));
    }

    public void reload() {
        loadMessages();
    }

    private String colorize(String text) {
        return text.replace("&", "§");
    }
}
