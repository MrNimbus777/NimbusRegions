package ru.nimbus.regions;

import java.util.ArrayList;

public enum FlagType {
    PVP,
    PVE,
    USE,
    MOB_SPAWNING,
    INVINCIBLE,
    DROP_ITEMS,
    PICKUP_ITEMS,
    BLOCK_PLACE,
    BLOCK_BREAK,
    MOB_GRIEFING,
    ENTRY,
    BLOCKED_COMMANDS,
    PISTON,
    OPEN_BLOCK_INVENTORY;

    public static ArrayList<String> getNames(){
        ArrayList<String> list = new ArrayList<>();
        for(FlagType flag : FlagType.values()){
            list.add(flag.name());
        }
        return list;
    }
}