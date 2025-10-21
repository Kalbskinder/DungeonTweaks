package net.kalbskinder.utils;

/**
 * Utility to remove Minecraft color & format codes (e.g. §a, §6, §l).
 */
public class NameUtils {

    public String stripColorCodes(String s) {
        if (s == null) return "";
        // Remove all §x color/format codes (case-insensitive)
        return s.replaceAll("(?i)§[0-9A-FK-OR]", "");
    }
}