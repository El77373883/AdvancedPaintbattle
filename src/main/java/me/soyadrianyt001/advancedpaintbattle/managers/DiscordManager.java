package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordManager {

    private final AdvancedPaintBattle plugin;
    private final boolean enabled;
    private final String webhookUrl;

    public DiscordManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getConfig().getBoolean("discord.enabled", false);
        this.webhookUrl = plugin.getConfig().getString("discord.webhook-url", "");
    }

    public void announceWinner(String playerName, String arena, int points) {
        if (!enabled || webhookUrl.isEmpty()) return;
        String json = "{\"embeds\":[{\"title\":\"🏆 AdvancedPaintBattle - Ganador!\",\"description\":\"**" +
                playerName + "** gano en la arena **" + arena + "** con **" + points + "** puntos!\",\"color\":16766720}]}";
        sendWebhook(json);
    }

    public void announceTournament() {
        if (!enabled || webhookUrl.isEmpty()) return;
        String json = "{\"embeds\":[{\"title\":\"🎨 Torneo Semanal Iniciado!\",\"description\":\"Conéctate y usa /apb join para participar.\",\"color\":16766720}]}";
        sendWebhook(json);
    }

    private void sendWebhook(String json) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(webhookUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(json.getBytes(StandardCharsets.UTF_8));
                    }
                    conn.getResponseCode();
                    conn.disconnect();
                } catch (Exception e) {
                    plugin.getLogger().warning("[APB] Error enviando webhook Discord: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
