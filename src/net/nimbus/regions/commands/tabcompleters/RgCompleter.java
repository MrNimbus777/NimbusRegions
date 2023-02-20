package ru.nimbus.regions.commands.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ru.nimbus.regions.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RgCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        ArrayList<String> all = new ArrayList<>();
        if(args.length == 1){
            ArrayList<String> list = new ArrayList<>();
            if("create".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("create");
            if("claim".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("claim");
            if("flag".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("flag");
            if("flags".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("flags");
            if("info".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("info");
            if("i".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("i");
            if("addmember".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("addmember");
            if("addowner".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("addowner");
            if("removemember".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("removemember");
            if("removeowner".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("removeowner");
            if("sell".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("sell");
            if("select".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("select");
            if("give".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("give");
            if("remove".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("remove");
            if("delete".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("delete");
            if("create".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("create");
            if("list".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("list");
            if("mylist".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("mylist");
            if("show".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("show");
            if(sender.hasPermission("nrg.setadmin")) if("setadmin".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("setadmin");
            if("movehere".startsWith(args[0].toLowerCase(Locale.ROOT))) list.add("movehere");
            return list;
        } else if(args.length == 2){
            all = new ArrayList<>(Region.regions.keySet());
            if(args[0].equalsIgnoreCase("flag") || args[0].equalsIgnoreCase("addmember") || args[0].equalsIgnoreCase("removemember")) {
                if(!sender.hasPermission("nrg.admin.creator") && !sender.hasPermission("nrg.admin.owner")){
                    ArrayList<String> list = new ArrayList<>();
                    for(Region rg : Region.getRegions()){
                        if((rg.getCreator().equals(sender.getName()) || rg.getOwners().contains(sender.getName())) && args[1].toLowerCase(Locale.ROOT).startsWith(rg.getName().toLowerCase(Locale.ROOT))){
                            list.add(rg.getName());
                        }
                    }
                    return list;
                } else {
                    ArrayList<String> list = new ArrayList<>();
                    for(String elem : all){
                        if(args[1].toLowerCase(Locale.ROOT).startsWith(elem.toLowerCase(Locale.ROOT))) list.add(elem);
                    }
                    return list;
                }
            } else if(args[0].equalsIgnoreCase("addowner") || args[0].equalsIgnoreCase("removeowner") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("movehere") || args[0].equalsIgnoreCase("give")){
                if(!sender.hasPermission("nrg.admin.creator")){
                    ArrayList<String> list = new ArrayList<>();
                    for(Region rg : Region.getRegions()){
                        if(rg.getCreator().equals(sender.getName()) && args[1].toLowerCase(Locale.ROOT).startsWith(rg.getName().toLowerCase(Locale.ROOT))){
                            list.add(rg.getName());
                        }
                    }
                    return list;
                } else {
                    ArrayList<String> list = new ArrayList<>();
                    for(String elem : all){
                        if(args[1].toLowerCase(Locale.ROOT).startsWith(elem.toLowerCase(Locale.ROOT))) list.add(elem);
                    }
                    return list;
                }
            }
        }
        return new ArrayList<>();
    }
}
