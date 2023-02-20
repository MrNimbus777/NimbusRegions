package ru.nimbus.regions;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.nimbus.regions.commands.executors.RgCommand;
import ru.nimbus.regions.commands.tabcompleters.RgCompleter;
import ru.nimbus.regions.events.player.BlockPlaceEvents;
import ru.nimbus.regions.events.player.PlayerInteractEvents;

import java.io.File;
import java.util.*;

public class Main extends JavaPlugin {

    public static Main a;
    public static String pref;
    public static String name;

    public static ArrayList<String> playersCreating = new ArrayList<>();
    public static HashMap<Player, String> pos1 = new HashMap<>();
    public static HashMap<Player, String> pos2 = new HashMap<>();
    public static HashMap<FlagType, String> defaultFlags = new HashMap<>();

    Permission permission = null;
    boolean vault = false;

    // Load from config
    public static Material selection_tool;

    public void loadEvents() {
        getServer().getPluginManager().registerEvents(new PlayerInteractEvents(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceEvents(), this);
    }

    public void loadCommands() {
        getCommand("rg").setExecutor(new RgCommand());

        getCommand("rg").setTabCompleter(new RgCompleter());
    }

    public void onEnable() {
        a = this;

        File config_file = new File(getDataFolder() + File.separator + getName() + File.separator + "config.yml");
        if (!config_file.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }

        loadEvents();
        loadCommands();

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            vault = true;
            permission = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
            ArrayList<String> groups = new ArrayList<>(List.of(permission.getGroups()));
            for(String str : getConfig().getConfigurationSection("Settings.groups").getKeys(false)){
                if(groups.contains(str)){
                    int maxRegionAmount = getConfig().getInt("Settings.groups."+str+".maxRegionAmount");
                    int maxRegionArea = getConfig().getInt("Settings.groups."+str+".maxRegionArea");
                    new PlayerGroup(str, maxRegionArea, maxRegionAmount);
                }
            }
        } else {
            vault = false;
            new PlayerGroup("default", 2500, 3);
        }
        pref = getConfig().getString("Server.prefix").replace("&", "\u00a7");
        name = getConfig().getString("Server.name").replace("&", "\u00a7");

        // Config

        selection_tool = Material.getMaterial(getConfig().getString("Settings.selection_tool").toUpperCase(Locale.ROOT));


        for (String creator : YmlFile.getDirList("Regions")) {
            YmlFile.exists("Regions", creator, false, true);
            YmlFile file = YmlFile.get("Regions", creator);
            FileConfiguration conf = file.getConfig();
            if (conf.getConfigurationSection("Regions") != null)
                for (String name : conf.getConfigurationSection("Regions").getKeys(false)) {
                    String world = conf.getString("Regions." + name + ".world");
                    int xOne = conf.getInt("Regions." + name + ".xOne");
                    int zOne = conf.getInt("Regions." + name + ".zOne");
                    int xTwo = conf.getInt("Regions." + name + ".xTwo");
                    int zTwo = conf.getInt("Regions." + name + ".zTwo");
                    Region rg = new Region(name, world, creator, xOne, xTwo, zOne, zTwo);
                    ArrayList<String> owners = new ArrayList<>();
                    if (!conf.getStringList("Regions." + name + ".owners").isEmpty())
                        owners = new ArrayList<>(conf.getStringList("Regions." + name + ".owners"));
                    ArrayList<String> members = new ArrayList<>();
                    if (!conf.getStringList("Regions." + name + ".members").isEmpty())
                        members = new ArrayList<>(conf.getStringList("Regions." + name + ".members"));
                    HashMap<FlagType, String> flags = new HashMap<>();
                    if (conf.getConfigurationSection("Region." + name + "flags") != null)
                        for (String flag : conf.getConfigurationSection("Region." + name + "flags").getKeys(false)) {
                            flags.put(FlagType.valueOf(flag), conf.getString("Region." + name + "flags." + flag));
                        }
                    rg.setMembers(members);
                    rg.setOwners(owners);
                    rg.setFlags(flags);
                    rg.setAdmin(conf.getBoolean("Regions." + name + ".isAdmin"));
                    if (conf.getBoolean("Region." + name + ".isCuboid")) {
                        rg.setCuboid(conf.getInt("Region." + name + ".yOne"), conf.getInt("Region." + name + ".yTwo"));
                    }
                }
        }

        for (String str : getConfig().getConfigurationSection("Settings.default_flags").getKeys(false)) {
            if (FlagType.getNames().contains(str)) {
                FlagType flagType = FlagType.valueOf(str);
                String value = getConfig().getString("Settings.default_flags."+str);
                defaultFlags.put(flagType, value);
            }
        }


    }

    public void onDisable() {
        saveConfig();
        for (YmlFile file : YmlFile.hash.values()) {
            file.save();
        }
    }
}
