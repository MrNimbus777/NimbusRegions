package ru.nimbus.regions;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerGroup {
    int maxRegionArea;
    int maxRegionAmount;
    String name;
    public static HashMap<String, PlayerGroup> playerGroups = new HashMap<>();

    public PlayerGroup(String name, int maxRegionArea, int maxRegionAmount){
        this.maxRegionArea = maxRegionArea;
        this.maxRegionAmount = maxRegionAmount;
        this.name = name;

        playerGroups.put(name, this);
    }

    public static PlayerGroup getGroup(Player p){
        String group;
        if(Main.a.vault) {
            group = Main.a.permission.getPrimaryGroup(p);
        } else group = "default";
        return playerGroups.get(group);
    }

    public int getMaxRegionArea(){
        return maxRegionArea;
    }
    public int getMaxRegionAmount() {
        return maxRegionAmount;
    }
}
