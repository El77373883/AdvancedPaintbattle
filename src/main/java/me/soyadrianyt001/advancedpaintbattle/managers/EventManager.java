package me.soyadrianyt001.advancedpaintbattle.managers;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import org.bukkit.Bukkit;

import java.time.LocalDate;

public class EventManager {

    private final AdvancedPaintBattle plugin;
    private String activeEvent = null;

    public EventManager(AdvancedPaintBattle plugin) {
        this.plugin = plugin;
    }

    public void checkSpecialEvents() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        if (plugin.getConfig().getBoolean("events.halloween.enabled") && month == 10) {
            activeEvent = "HALLOWEEN";
            Bukkit.getConsoleSender().sendMessage("§6[APB] §eEvento Halloween activado!");
        } else if (plugin.getConfig().getBoolean("events.christmas.enabled") && month == 12) {
            activeEvent = "CHRISTMAS";
            Bukkit.getConsoleSender().sendMessage("§6[APB] §eEvento Navidad activado!");
        } else if (plugin.getConfig().getBoolean("events.new-year.enabled") && month == 1 && day == 1) {
            activeEvent = "NEW_YEAR";
            Bukkit.getConsoleSender().sendMessage("§6[APB] §eEvento Año Nuevo activado!");
        }
    }

    public boolean isEventActive() { return activeEvent != null; }
    public String getActiveEvent() { return activeEvent; }

    public String getEventThemeCategory() {
        if ("HALLOWEEN".equals(activeEvent)) return "terror";
        if ("CHRISTMAS".equals(activeEvent)) return "navidad";
        return null;
    }
}
