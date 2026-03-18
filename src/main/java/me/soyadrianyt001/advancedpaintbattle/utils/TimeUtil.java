package me.soyadrianyt001.advancedpaintbattle.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String format(int seconds) {
        if (seconds >= 3600) {
            int h = seconds / 3600;
            int m = (seconds % 3600) / 60;
            int s = seconds % 60;
            return String.format("%02d:%02d:%02d", h, m, s);
        } else if (seconds >= 60) {
            int m = seconds / 60;
            int s = seconds % 60;
            return String.format("%02d:%02d", m, s);
        } else {
            return seconds + "s";
        }
    }

    public static String formatLong(long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long days = TimeUnit.MILLISECONDS.toDays(millis);

        if (days > 0) return days + " dia" + (days > 1 ? "s" : "");
        if (hours > 0) return hours + " hora" + (hours > 1 ? "s" : "");
        if (minutes > 0) return minutes + " minuto" + (minutes > 1 ? "s" : "");
        return seconds + " segundo" + (seconds > 1 ? "s" : "");
    }

    public static String formatDate(long millis) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(millis));
    }

    public static String getColoredTime(int seconds) {
        if (seconds > 30) return "§a" + format(seconds);
        if (seconds > 10) return "§e" + format(seconds);
        return "§c" + format(seconds);
    }

    public static String timeSince(long millis) {
        long diff = System.currentTimeMillis() - millis;
        return formatLong(diff) + " atras";
    }

    public static boolean isToday(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date(millis)).equals(sdf.format(new Date()));
    }
}
