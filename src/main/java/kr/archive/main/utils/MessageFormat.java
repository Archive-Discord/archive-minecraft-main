package kr.archive.main.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageFormat {
    public static String WarnnigMessage(String message) {
        return ChatColor.AQUA + "[ Archive ] " + ChatColor.BOLD + ChatColor.DARK_RED + message;
    }

    public static String SuccessMessage(String message) {
        return ChatColor.AQUA + "[ Archive ] " + ChatColor.WHITE + message;
    }

    public static String ErrorMessage(String message) {
        return ChatColor.AQUA + "[ Archive ] " + ChatColor.BOLD + ChatColor.RED + message;
    }

}
