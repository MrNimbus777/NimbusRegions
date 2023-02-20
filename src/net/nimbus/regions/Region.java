package ru.nimbus.regions;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Region {
    public static HashMap<String, Region> regions = new HashMap<>();
    String regionName;
    String world;
    String creator;
    int xOne;
    int xTwo;
    int zOne;
    int zTwo;
    int yOne;
    int yTwo;
    boolean isAdmin = false;
    boolean isCuboid = false;
    HashMap<FlagType, String> regionflags = new HashMap<FlagType, String>();
    ArrayList<String> owners = new ArrayList<String>();
    ArrayList<String> members = new ArrayList<String>();
    public Region(String regionName, String world, String creator, int xOne, int xTwo, int zOne, int zTwo){
        this.xOne = xOne;
        this.xTwo = xTwo;
        this.zOne = zOne;
        this.zTwo = zTwo;
        this.creator = creator;
        this.regionName = regionName;
        this.world = world;
        yOne = 0;
        yTwo = Bukkit.getWorld(world).getMaxHeight();

        regions.put(regionName, this);
    }
    public static Region getRegion(String regionName) {
        return regions.get(regionName);
    }
    public static ArrayList<Region> getRegions(String world, int x, int z) {
        ArrayList<Region> list = new ArrayList<>();
        for(Region rg : regions.values()) {
            if(rg.getx() <= x && rg.getX() >= x && rg.getz() <= z && rg.getZ() >= z && rg.getWorld().equals(world)) {
                list.add(rg);
            }
        }
        return list;
    }
    public static ArrayList<Region> getRegions(String world, int x, int z, int y) {
        ArrayList<Region> list = new ArrayList<>();
        for(Region rg : regions.values()) {
            if(rg.getx() <= x && rg.getX() >= x && rg.getz() <= z && rg.getZ() >= z && rg.gety() <= y && rg.getY() >= y && rg.getWorld().equals(world)) {
                list.add(rg);
            }
        }
        return list;
    }
    public static ArrayList<Region> getRegions(ArrayList<Region> regions, String world, int x, int z) {
        ArrayList<Region> list = new ArrayList<>();
        int i = 0;
        for(Region rg : regions) {
            if(rg.getx() <= x && rg.getX() >= x && rg.getz() <= z && rg.getZ() >= z && rg.getWorld().equals(world)) {
                list.add(rg);
                regions.remove(i);
            }
            i++;
        }
        return list;
    }
    public static ArrayList<Region> getRegions(ArrayList<Region> regions, String world, int x, int y, int z) {
        ArrayList<Region> list = new ArrayList<>();
        int i = 0;
        for(Region rg : regions) {
            if(rg.getx() <= x && rg.getX() >= x && rg.getz() <= z && rg.getZ() >= z && rg.gety() <= y && rg.getY() >= y && rg.getWorld().equals(world)) {
                list.add(rg);
                regions.remove(i);
            }
            i++;
        }
        return list;
    }
    public static Region getSmallestRegion(String world, int x, int z, int y) {
        ArrayList<Region> list = new ArrayList<>(regions.values());
        Region smallest = null;
        int ord = 0;
        for(Region rg : list) {
            if(rg.getx() <= x && rg.getX() >= x && rg.getz() <= z && rg.getZ() >= z && rg.gety() <= y && rg.getY() >= y && rg.getWorld().equals(world)) {
                ord++;
                if(ord == 1){
                    smallest = rg;
                }
                else if((rg.getX()- rg.getx()+1)*(rg.getZ()- rg.getz()+1) < (smallest.getX()- smallest.getx()+1)*(smallest.getZ()- smallest.getz()+1)){
                    smallest = rg;
                }
            }
        }
        return smallest;
    }
    public static Collection<Region> getRegions() {
        return regions.values();
    }
    public String getCreator() {
        return this.creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }
    public String getName() {
        return this.regionName;
    }
    public int getx() {
        return this.xOne;
    }
    public int getX() {
        return this.xTwo;
    }
    public int getz() {
        return this.zOne;
    }
    public int getZ() {
        return this.zTwo;
    }
    public int gety() {
        return this.yOne;
    }
    public int getY() {
        return this.yTwo;
    }
    public void setx(int x) {
        this.xOne = x;
    }
    public void setX(int X) {
        this.xTwo = X;
    }
    public void setz(int z) {
        this.zOne = z;
    }
    public void setZ(int Z) {
        this.zTwo = Z;
    }
    public String getWorld() {
        return this.world;
    }
    public void setWorld(String world) {
        this.world = world;
    }
    public HashMap<FlagType, String> getFlags() {
        return regionflags;
    }
    public void setFlags(HashMap<FlagType, String> regionflags) {
        this.regionflags = regionflags;
    }
    public ArrayList<String> getMembers(){
        return this.members;
    }
    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }
    public ArrayList<String> getOwners(){
        return this.owners;
    }
    public void setOwners(ArrayList<String> owners) {
        this.owners = owners;
    }

    public void setCuboid(int yOne, int yTwo) {
        isCuboid = true;
        this.yOne = yOne;
        this.yTwo = yTwo;
    }
    public void setCuboid() {
        isCuboid = false;
    }
    public void setAdmin(boolean isAdmin){
        this.isAdmin = isAdmin;
    }
    public boolean isAdmin(){
        return this.isAdmin;
    }
    /**
     * Saves the region in the file.
     */
    public void save() {
        YmlFile file = YmlFile.get("Regions", creator);
        FileConfiguration conf = file.getConfig();
        conf.set("Regions."+regionName+".xOne", xOne);
        conf.set("Regions."+regionName+".xTwo", xTwo);
        conf.set("Regions."+regionName+".zOne", zOne);
        conf.set("Regions."+regionName+".zTwo", zTwo);
        conf.set("Regions."+regionName+".creator", creator);
        conf.set("Regions."+regionName+".world", world);
        conf.set("Regions."+regionName+".isAdmin", isAdmin);
        if(isCuboid){
            conf.set("Regions."+regionName+".isCuboid", true);
            conf.set("Regions."+regionName+".yOne", yOne);
            conf.set("Regions."+regionName+".yTwo", yTwo);
        } else {
            conf.set("Regions."+regionName+".yOne", null);
            conf.set("Regions."+regionName+".yTwo", null);
        }
        if(!members.isEmpty())conf.set("Regions."+regionName+".members", members);
        if(!owners.isEmpty())conf.set("Regions."+regionName+".owners", owners);
        if(!regionflags.isEmpty()) {
            conf.set("Regions."+getName()+".flags", null);
            for(FlagType type : regionflags.keySet()) {
                conf.set("Regions."+getName()+".flags."+type.name(), regionflags.get(type));
            }
        }
        file.setConfig(conf);
        file.save();
    }

    /**
     * Deletes the region into file.
     */
    public void delete() {
        YmlFile file = YmlFile.get("Regions", creator);
        FileConfiguration conf = file.getConfig();
        regions.remove(regionName);
        conf.set("Regions."+regionName, null);
        file.setConfig(conf);
        file.save();
    }
}