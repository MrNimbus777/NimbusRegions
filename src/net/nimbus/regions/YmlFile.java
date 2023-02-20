package ru.nimbus.regions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class YmlFile {

    public static final HashMap<String, YmlFile> hash = new HashMap<>();

    FileConfiguration configuration;
    File file;
    String name;
    String dir;

    public YmlFile(String dir, String name) {
        this.dir = dir;
        this.name = name;

        hash.put(dir+"/"+name, this);
    }
    public File getFile(){
        return this.file;
    }
    public static YmlFile get(String dir, String name) {
        return hash.get(dir+"/"+name);
    }

    public FileConfiguration getConfig() {
        return configuration;
    }

    public void setConfig(FileConfiguration configuration) {
         this.configuration = configuration;
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YmlFile create() {
        file = new File(Main.a.getDataFolder()+"/"+ dir, name+".yml");
        if(!file.exists()) {
            File fd = new File(Main.a.getDataFolder() +"/"+ dir);
            if(!fd.exists()) {
                fd.mkdir();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        configuration = YamlConfiguration.loadConfiguration(file);
        return this;
    }

    public String getName() {
        return name;
    }

    public static boolean exists(String dir, String name, boolean create, boolean getFromFolder){
        if(!hash.isEmpty())if(hash.containsKey(dir+"/"+name)){
            return true;
        }
        if(create){
            YmlFile file = new YmlFile(dir, name);
            file.create();
            return true;
        }
        if((new File(Main.a.getDataFolder() + "/" + dir + "/" + name+".yml")).exists() && getFromFolder){
            YmlFile file = new YmlFile(dir, name);
            file.create();
            return true;
        } return false;
    }


    public static ArrayList<String> getDirList(String dir){
        ArrayList<String> list = new ArrayList<>();
        if(new File(Main.a.getDataFolder() + "/" + dir).exists()) list = (ArrayList<String>) Arrays.asList(new File(Main.a.getDataFolder() + "/" + dir).list());
        return list;
    }
}
