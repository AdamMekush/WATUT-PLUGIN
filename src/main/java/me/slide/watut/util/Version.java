package me.slide.watut.util;

import org.bukkit.Bukkit;

public class Version {
    public static boolean isAbove1_20_2OrEqual(){
        Integer[] version = getVersion(Bukkit.getBukkitVersion().split("-")[0]);
        Integer major = version[0];
        Integer minor = version[1];
        Integer patch = version[2];
        return (major > 1) || (major == 1 && (minor > 20 || (minor == 20 && (patch != null && patch >= 2))));
    }

    public static Integer[] getVersion(String stringVersion){
        String[] parts = stringVersion.split("\\.");
        Integer[] versionNumbers = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            versionNumbers[i] = parseIntOrNull(parts[i]);
        }
        return versionNumbers;
    }

    private static Integer parseIntOrNull(String str) {
        try {
            return (str != null) ? Integer.parseInt(str) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isOlderVersion(Integer[] version, Integer[] versionToCheck) {
        int length = Math.max(version.length, versionToCheck.length);

        for (int i = 0; i < length; i++) {
            Integer v1 = (i < version.length) ? version[i] : 0;
            Integer v2 = (i < versionToCheck.length) ? versionToCheck[i] : 0;

            if (v1 < v2) {
                return true;
            } else if (v1 > v2) {
                return false;
            }
        }
        return false;
    }
}
