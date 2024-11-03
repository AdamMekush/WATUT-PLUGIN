package me.slide.watut.util;

import org.bukkit.Bukkit;

public class Version {
    public static boolean isAbove1_20_2OrEqual(){
        String[] version = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        Integer major = parseIntOrNull(version[0]);
        Integer minor = parseIntOrNull(version[1]);
        Integer patch = parseIntOrNull(version[2]);
        return (major > 1) || (major == 1 && (minor > 20 || (minor == 20 && (patch != null && patch >= 2))));
    }

    private static Integer parseIntOrNull(String str) {
        try {
            return (str != null) ? Integer.parseInt(str) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
