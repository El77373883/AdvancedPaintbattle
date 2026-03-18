package me.soyadrianyt001.advancedpaintbattle.utils;

import me.soyadrianyt001.advancedpaintbattle.AdvancedPaintBattle;
import me.soyadrianyt001.advancedpaintbattle.models.GameSession;

import java.time.LocalTime;
import java.util.*;

public class ThemeUtil {

    public static String selectTheme(AdvancedPaintBattle plugin, GameSession session) {
        String eventCategory = plugin.getEventManager().getEventThemeCategory();
        Random rand = new Random();

        // Tema Leyenda (muy raro)
        if (rand.nextInt(100) < 3) {
            List<String> legendary = plugin.getConfig().getStringList("themes.leyenda");
            if (!legendary.isEmpty()) return legendary.get(rand.nextInt(legendary.size()));
        }

        // Tema secreto (raro)
        if (rand.nextInt(100) < 8) {
            List<String> secrets = plugin.getConfig().getStringList("themes.secretos");
            if (!secrets.isEmpty()) return secrets.get(rand.nextInt(secrets.size()));
        }

        // Evento especial
        if (eventCategory != null && plugin.getConfig().contains("themes." + eventCategory)) {
            List<String> eventThemes = plugin.getConfig().getStringList("themes." + eventCategory);
            if (!eventThemes.isEmpty()) return eventThemes.get(rand.nextInt(eventThemes.size()));
        }

        // IA por hora del dia
        LocalTime time = LocalTime.now();
        String category;
        int hour = time.getHour();
        if (hour >= 6 && hour < 12) category = "naturaleza";
        else if (hour >= 12 && hour < 18) category = "comida";
        else if (hour >= 18 && hour < 22) category = "objetos";
        else category = "animales";

        List<String> themes = plugin.getConfig().getStringList("themes." + category);
        if (!themes.isEmpty()) return themes.get(rand.nextInt(themes.size()));

        return "Casa";
    }
}
