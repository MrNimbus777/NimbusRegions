package ru.nimbus.regions;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    static Pattern pattern = Pattern.compile("&[a-fA-F0-9]{6}");
    public static void send(Player p, String str){
        p.sendMessage(color(str));
    }
    public static String color(String str){
        Matcher match = pattern.matcher(str);
        while (match.find()) {
            String color = str.substring(match.start() + 1, match.end());
            str = str.replace("&" + color, ChatColor.of(color) + "");
            match = pattern.matcher(str);
        }
        return str.replace("&", "\u00a7");
    }
    public static String prefix(String s){
        return Main.pref+s;
    }

    public static String getLocale(Player p){
        return "en";
    }
}
