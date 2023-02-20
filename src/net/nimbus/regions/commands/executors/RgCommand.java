package ru.nimbus.regions.commands.executors;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.nimbus.regions.*;

public class RgCommand implements CommandExecutor {

    ArrayList<ArrayList<Location>> locations = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player p) {
            String locale = Message.getLocale(p);
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("sel") || args[0].equalsIgnoreCase("select")) {
                    if (!Main.playersCreating.contains(p.getName())) {
                        Main.playersCreating.add(p.getName());
                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.sell-enabled")));
                    } else {
                        Main.playersCreating.remove(p.getName());
                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.sell-disabled")));
                        Main.pos1.remove(p);
                        Main.pos2.remove(p);
                    }
                } else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("claim")) {
                    if (Main.pos1.containsKey(p)) {
                        if (Main.pos2.containsKey(p)) {
                            if (args.length >= 2) {
                                if (Region.getRegion(args[1]) == null) {
                                    boolean isAdmin = false;
                                    boolean isCuboid = false;
                                    if (args.length >= 4)
                                        if (p.hasPermission("rg.admin")) isAdmin = args[3].equalsIgnoreCase("admin");
                                    if (args.length >= 3)
                                        if (p.hasPermission("rg.cuboid")) isCuboid = args[2].equalsIgnoreCase("cuboid");
                                    int rgs = 0;
                                    for (Region rg1 : Region.getRegions()) {
                                        if (rg1.getCreator().equals(p.getName())) if (!rg1.isAdmin()) rgs++;
                                    }
                                    if (rgs < PlayerGroup.getGroup(p).getMaxRegionAmount() || isAdmin) {
                                        String[] cords1 = Main.pos1.get(p).split(",");
                                        String[] cords2 = Main.pos2.get(p).split(",");
                                        int x1 = Integer.parseInt(cords1[0]);
                                        int x2 = Integer.parseInt(cords2[0]);
                                        int z1 = Integer.parseInt(cords1[1]);
                                        int z2 = Integer.parseInt(cords2[1]);
                                        int y1 = Integer.parseInt(cords1[2]);
                                        int y2 = Integer.parseInt(cords2[2]);
                                        if (x1 > x2) {
                                            x2 = x1;
                                            x1 = Integer.parseInt(cords2[0]);
                                        }
                                        if (z1 > z2) {
                                            z2 = z1;
                                            z1 = Integer.parseInt(cords2[1]);
                                        }
                                        if (y1 > y2) {
                                            y2 = y1;
                                            y1 = Integer.parseInt(cords2[2]);
                                        }
                                        String world = cords1[3];
                                        int area = ((x2 - x1) + 1) * ((z2 - z1) + 1);
                                        if (area <= PlayerGroup.getGroup(p).getMaxRegionArea() || isAdmin) {
                                            ArrayList<Region> regions = new ArrayList<>(Region.getRegions());
                                            if (!isAdmin) if (isCuboid) {
                                                for (int x = x1; x <= x2; x++) {
                                                    for (int z = z1; z <= z2; z++) {
                                                        for (Region reg : Region.getRegions(regions, world, x, z)) {
                                                            if (!reg.getCreator().equals(p.getName())) {
                                                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.intersecting").replace("{cords}", "x = " + x + ", z = " + z)));
                                                                return true;
                                                            }
                                                        }
                                                    }
                                                }
                                            } else for (int x = x1; x <= x2; x++) {
                                                for (int z = z1; z <= z2; z++) {
                                                    for (int y = y1; y <= y2; y++) {
                                                        for (Region reg : Region.getRegions(regions, world, x, z)) {
                                                            if (!reg.getCreator().equals(p.getName())) {
                                                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.intersecting").replace("{cords}", "x = " + x + ", z = " + z)));
                                                                return true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            Region rg = new Region(args[1], world, p.getName(), x1, x2, z1, z2);
                                            if (isCuboid) rg.setCuboid(y1, y2);
                                            rg.setAdmin(isAdmin);
                                            rg.setFlags(new HashMap<>(Main.defaultFlags));
                                            rg.save();
                                            Main.playersCreating.remove(p.getName());
                                            Main.pos1.remove(p);
                                            Main.pos2.remove(p);
                                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.rg-created").replace("{rgName}", args[1]).replace("{area}", "" + area)));
                                        } else
                                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.to-big-area")));
                                    } else
                                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.to-many-rg")));
                                } else
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.rg-existing")));
                            } else
                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Usage.create")));
                        } else
                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.no-sellection")));
                    } else
                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.no-sellection")));
                } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
                    if (args.length >= 2) {
                        if (Region.getRegion(args[1]) != null) {
                            Region rg = Region.getRegion(args[1]);
                            if (rg.getCreator().equals(p.getName()) || p.hasPermission("nrg.region.creator")) {
                                rg.delete();
                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.rg-deleted").replace("{rgName}", args[1])));
                            } else
                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.not-creator")));
                        } else
                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.rg-inexisting")));
                    } else
                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Usage.remove")));
                } else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) {
                    if (args.length == 1) {
                        String noRegion = Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.no-region-here");
                        for (Region rg : Region.getRegions(p.getWorld().getName(), p.getLocation().getBlockX(), p.getLocation().getBlockZ())) {
                            noRegion = "";
                            String pos1 = "x = " + rg.getx() + ", z = " + rg.getz();
                            String pos2 = "x = " + rg.getX() + ", z = " + rg.getZ();
                            String area = (rg.getX() - rg.getx() + 1) * (rg.getZ() - rg.getz() + 1) + "";

                            String members = "";
                            for (String memb : rg.getMembers()) {
                                members = members + "&1,&b " + memb;
                            }

                            if (members == "") {
                                members = "empty";
                            } else {
                                members = members.replaceFirst("&1,&b ", "");
                            }

                            String owners = "";
                            for (String own : rg.getOwners()) {
                                owners = owners + "&1,&b " + own;
                            }
                            if (owners == "") {
                                owners = "empty";
                            } else {
                                owners = owners.replaceFirst("&1,&b ", "");
                            }

                            String flags = "";
                            for (FlagType type : rg.getFlags().keySet()) {
                                flags = flags + "&1,&b " + type.name() + ": &9" + rg.getFlags().get(type);
                            }
                            if (flags == "") {
                                flags = "empty";
                            } else {
                                flags = flags.replaceFirst("&1,&b ", "");
                            }

                            for (String message : Main.a.getConfig().getStringList("Messages." + locale + ".Regions.Info")) {
                                message = message.replace("{pos1}", pos1).replace("{pos2}", pos2).replace("{area}", area).replace("{members}", members).replace("{owners}", owners).replace("{creator}", rg.getCreator()).replace("{name}", rg.getName()).replace("{world}", rg.getWorld()).replace("{flags}", flags);
                                p.sendMessage("  " + message);
                            }
                            p.sendMessage(" ");
                        }
                        if (noRegion != "") {
                            Message.send(p, Message.prefix(noRegion));
                        }
                    } else {
                        Region rg = Region.getRegion(args[1]);
                        if (rg != null) {
                            String pos1 = "x = " + rg.getx() + ", z = " + rg.getz();
                            String pos2 = "x = " + rg.getX() + ", z = " + rg.getZ();
                            String area = (rg.getX() - rg.getx() + 1) * (rg.getZ() - rg.getz() + 1) + "";

                            String members = "";
                            for (String memb : rg.getMembers()) {
                                members = members + "&9,&b " + memb;
                            }

                            if (members == "") {
                                members = "empty";
                            } else {
                                members = members.replaceFirst("&9,&b ", "");
                            }

                            String owners = "";
                            for (String own : rg.getOwners()) {
                                owners = owners + "&9,&b " + own;
                            }
                            if (owners == "") {
                                owners = "empty";
                            } else {
                                owners = owners.replaceFirst("&9,&b ", "");
                            }

                            String flags = "";
                            for (FlagType type : rg.getFlags().keySet()) {
                                flags = flags + "&9,&b " + type.name() + ": &1" + rg.getFlags().get(type);
                            }
                            if (flags == "") {
                                flags = "empty";
                            } else {
                                flags = flags.replaceFirst("&9,&b  ", "");
                            }

                            p.sendMessage(" ");
                            for (String message : Main.a.getConfig().getStringList("Messages." + locale + ".Regions.Info")) {
                                message = message.replace("{pos1}", pos1).replace("{pos2}", pos2).replace("{area}", area).replace("{members}", members).replace("{owners}", owners).replace("{creator}", rg.getCreator()).replace("{name}", rg.getName()).replace("{world}", rg.getWorld()).replace("{flags}", flags);
                                p.sendMessage(" " + message);
                            }
                            p.sendMessage(" ");
                        } else
                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.rg-inexisting")));
                    }
                } else if (args[0].equalsIgnoreCase("flag")) {
                    if (args.length >= 4) {
                        Region rg = Region.getRegion(args[1]);
                        if (rg != null) {
                            int can = 0;
                            if (rg.getOwners() != null) if (rg.getOwners().contains(p.getName())) can += 1;
                            if (rg.getCreator().equals(p.getName())) can += 1;
                            if (can > 0) {
                                if (FlagType.getNames().contains(args[2])) {
                                    if (p.hasPermission("nrg.flags." + args[2])) {
                                        if (args[3].equals("on") || args[3].equals("off")) {
                                            HashMap<FlagType, String> flags = rg.getFlags();
                                            flags.put(FlagType.valueOf(args[2]), args[3]);
                                            rg.setFlags(flags);
                                            rg.save();
                                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.flag-set").replace("{flag}", args[2]).replace("{value}", args[3])));
                                        } else if (args[3].equals("delete")) {
                                            HashMap<FlagType, String> flags = rg.getFlags();
                                            if (flags.containsKey(FlagType.valueOf(args[2]))) {
                                                flags.remove(FlagType.valueOf(args[2]));
                                                rg.setFlags(flags);
                                                rg.save();
                                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.flag-set").replace("{flag}", args[2]).replace("{value}", args[3])));
                                            } else
                                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.no-flag")));
                                        } else
                                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Usage.flag")));
                                    } else
                                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.flag-no-perms")));
                                } else
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.flag-inexisting")));
                            } else
                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.not-owner")));
                        } else
                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.rg-inexisting")));
                    } else
                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Usage.flag")));
                } else if (args[0].equalsIgnoreCase("flags")) {
                    String flagList = "List of the available flags: ";
                    for (FlagType type : FlagType.values()) {
                        if (p.hasPermission("nrg.flags." + type.name())) {
                            flagList = flagList + "&9, &b" + type.name();
                        }
                    }
                    if (flagList.equals("List of the available flags: ")) {
                        flagList = flagList + "&bempty";
                    } else {
                        flagList = flagList.replaceFirst("&9, ", "");
                    }
                    Message.send(p, Message.prefix(flagList));
                } else if (args[0].equalsIgnoreCase("addmember")) {
                    if (args.length >= 3) {
                        Region rg = Region.getRegion(args[1]);
                        if (rg != null) {
                            int can = 0;
                            if (rg.getOwners() != null)
                                if (rg.getOwners().contains(p.getName()) || p.hasPermission("nrg.region.creator"))
                                    can += 1;
                            if (rg.getCreator().equals(p.getName()) || p.hasPermission("nrg.region.creator")) can += 1;
                            if (can > 0) {
                                ArrayList<String> members = rg.getMembers();
                                if (!members.contains(args[2])) {
                                    members.add(args[2]);
                                    rg.setMembers(members);
                                    rg.save();
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.member-add").replace("{rgName}", args[1]).replace("{player}", args[2])));
                                } else
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.member-in")));
                            } else
                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.not-owner")));
                        } else
                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.rg-inexisting")));
                    } else
                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Usage.addmember")));
                } else if (args[0].equalsIgnoreCase("removemember")) {
                    if (args.length >= 3) {
                        Region rg = Region.getRegion(args[1]);
                        if (rg != null) {
                            int can = 0;
                            if (rg.getOwners() != null)
                                if (rg.getOwners().contains(p.getName()) || p.hasPermission("nrg.region.creator"))
                                    can += 1;
                            if (rg.getCreator().equals(p.getName()) || p.hasPermission("nrg.region.creator")) can += 1;
                            if (can > 0) {
                                ArrayList<String> members = rg.getMembers();
                                if (members.contains(args[2])) {
                                    members.remove(args[2]);
                                    rg.setMembers(members);
                                    rg.save();
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.member-remove").replace("{rgName}", args[1]).replace("{player}", args[2])));
                                } else
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.member-out")));
                            } else
                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.not-owner")));
                        } else
                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.rg-inexisting")));
                    } else
                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Usage.removemember")));
                } else if (args[0].equalsIgnoreCase("addowner")) {
                    if (args.length >= 3) {
                        Region rg = Region.getRegion(args[1]);
                        if (rg != null) {
                            int can = 0;
                            if (rg.getCreator().equals(p.getName()) || p.hasPermission("nrg.region.creator")) can += 1;
                            if (can > 0) {
                                ArrayList<String> owners = rg.getOwners();
                                if (!owners.contains(args[2])) {
                                    owners.add(args[2]);
                                    rg.setMembers(owners);
                                    rg.save();
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.owner-add").replace("{rgName}", args[1]).replace("{player}", args[2])));
                                } else
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.owner-in")));
                            } else
                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.not-creator")));
                        } else
                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.rg-inexisting")));
                    } else
                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Usage.addowner")));
                } else if (args[0].equalsIgnoreCase("removeowner")) {
                    if (args.length >= 3) {
                        Region rg = Region.getRegion(args[1]);
                        if (rg != null) {
                            int can = 0;
                            if (rg.getCreator().equals(p.getName()) || p.hasPermission("nrg.region.creator")) can += 1;
                            if (can > 0) {
                                ArrayList<String> owners = rg.getOwners();
                                if (owners.contains(args[2])) {
                                    owners.remove(args[2]);
                                    rg.setMembers(owners);
                                    rg.save();
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.owner-remove").replace("{rgName}", args[1]).replace("{player}", args[2])));
                                } else
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.owner-out")));
                            } else
                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.not-creator")));
                        } else
                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.rg-inexisting")));
                    } else
                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Usage.removeowner")));
                } else if (args[0].equalsIgnoreCase("list")) {
                    String rgs = "";
                    for (Region rg : Region.getRegions()) {
                        rgs = rgs + "&b, &f" + rg.getName();
                    }
                    rgs = rgs.replaceFirst("&b, &f", "");
                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.List").replace("{regions}", rgs)));
                } else if (args[0].equalsIgnoreCase("mylist")) {
                    String rgs1 = "";
                    for (Region rg : Region.getRegions()) {
                        if (rg.getCreator().equals(p.getName())) rgs1 = rgs1 + "&b, &f" + rg.getName();
                    }
                    rgs1 = rgs1.replaceFirst("&b, &f", "");

                    String rgs2 = "";
                    for (Region rg : Region.getRegions()) {
                        if (rg.getOwners() != null)
                            if (rg.getOwners().contains(p.getName())) rgs2 = rgs2 + "&b, &f" + rg.getName();
                    }
                    rgs2 = rgs2.replaceFirst("&b, &f", "");

                    String rgs3 = "";
                    for (Region rg : Region.getRegions()) {
                        if (rg.getMembers() != null)
                            if (rg.getMembers().contains(p.getName())) rgs3 = rgs3 + "&b, &f" + rg.getName();
                    }
                    rgs3 = rgs3.replaceFirst("&b, &f", "");
                    for (String str : Main.a.getConfig().getStringList("Messages." + locale + ".Regions.Mylist")) {
                        Message.send(p, Message.prefix(str.replace("{creator}", rgs1).replace("{owner}", rgs2).replace("{member}", rgs3)));
                    }
                } else if (args[0].equalsIgnoreCase("show")) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
                    int x = p.getLocation().getBlockX();
                    int z = p.getLocation().getBlockZ();
                    String world = p.getWorld().getName();
                    locations = new ArrayList<>();
                    for (Region rg : Region.getRegions()) {
                        if (rg.getx() - 80 <= x && rg.getX() + 80 >= x && rg.getz() - 80 <= z && rg.getZ() + 80 >= z && rg.getWorld().equals(world)) {
                            int higthest = p.getWorld().getHighestBlockAt(rg.getx(), rg.getz()).getY();

                            ArrayList<Integer> list = new ArrayList();
                            list.add(p.getWorld().getHighestBlockAt(rg.getX(), rg.getz()).getY());
                            list.add(p.getWorld().getHighestBlockAt(rg.getx(), rg.getZ()).getY());
                            list.add(p.getWorld().getHighestBlockAt(rg.getX(), rg.getZ()).getY());

                            for (Integer i : list) {
                                if (i > higthest) higthest = i;
                            }
                            higthest += 2;

                            ArrayList<Location> locationToRemove = new ArrayList<>();

                            int centerX = (rg.getX() + rg.getx()) / 2;
                            for (int nowX = 0; centerX + nowX < rg.getX() - 2; nowX += 5) {
                                p.sendBlockChange(new Location(p.getWorld(), centerX + nowX, higthest, rg.getz()), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
                                p.sendBlockChange(new Location(p.getWorld(), centerX + nowX, higthest, rg.getZ()), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
                                locationToRemove.add(new Location(p.getWorld(), centerX + nowX, higthest, rg.getz()));
                                locationToRemove.add(new Location(p.getWorld(), centerX + nowX, higthest, rg.getZ()));

                                p.sendBlockChange(new Location(p.getWorld(), centerX - nowX, higthest, rg.getz()), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
                                p.sendBlockChange(new Location(p.getWorld(), centerX - nowX, higthest, rg.getZ()), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
                                locationToRemove.add(new Location(p.getWorld(), centerX - nowX, higthest, rg.getz()));
                                locationToRemove.add(new Location(p.getWorld(), centerX - nowX, higthest, rg.getZ()));
                            }

                            int centerZ = (rg.getZ() + rg.getz()) / 2;
                            for (int nowZ = 0; centerZ + nowZ < rg.getZ() - 2; nowZ += 5) {
                                p.sendBlockChange(new Location(p.getWorld(), rg.getx(), higthest, centerZ + nowZ), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
                                p.sendBlockChange(new Location(p.getWorld(), rg.getX(), higthest, centerZ + nowZ), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
                                locationToRemove.add(new Location(p.getWorld(), rg.getx(), higthest, centerZ + nowZ));
                                locationToRemove.add(new Location(p.getWorld(), rg.getX(), higthest, centerZ + nowZ));

                                p.sendBlockChange(new Location(p.getWorld(), rg.getx(), higthest, centerZ - nowZ), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
                                p.sendBlockChange(new Location(p.getWorld(), rg.getX(), higthest, centerZ - nowZ), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
                                locationToRemove.add(new Location(p.getWorld(), rg.getx(), higthest, centerZ - nowZ));
                                locationToRemove.add(new Location(p.getWorld(), rg.getX(), higthest, centerZ - nowZ));
                            }
                            p.sendBlockChange(new Location(p.getWorld(), rg.getx(), higthest, rg.getz()), Material.SEA_LANTERN, (byte) 3);
                            p.sendBlockChange(new Location(p.getWorld(), rg.getx() + 1, higthest, rg.getz()), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
                            p.sendBlockChange(new Location(p.getWorld(), rg.getx(), higthest, rg.getz() + 1), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);

                            locationToRemove.add(new Location(p.getWorld(), rg.getx(), higthest, rg.getz()));
                            locationToRemove.add(new Location(p.getWorld(), rg.getx() + 1, higthest, rg.getz()));
                            locationToRemove.add(new Location(p.getWorld(), rg.getx(), higthest, rg.getz() + 1));

                            p.sendBlockChange(new Location(p.getWorld(), rg.getX(), higthest, rg.getz()), Material.SEA_LANTERN, (byte) 3);
                            p.sendBlockChange(new Location(p.getWorld(), rg.getX() - 1, higthest, rg.getz()), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
                            p.sendBlockChange(new Location(p.getWorld(), rg.getX(), higthest, rg.getz() + 1), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);

                            locationToRemove.add(new Location(p.getWorld(), rg.getX(), higthest, rg.getz()));
                            locationToRemove.add(new Location(p.getWorld(), rg.getX() - 1, higthest, rg.getz()));
                            locationToRemove.add(new Location(p.getWorld(), rg.getX(), higthest, rg.getz() + 1));

                            p.sendBlockChange(new Location(p.getWorld(), rg.getx(), higthest, rg.getZ()), Material.SEA_LANTERN, (byte) 3);
                            p.sendBlockChange(new Location(p.getWorld(), rg.getx() + 1, higthest, rg.getZ()), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
                            p.sendBlockChange(new Location(p.getWorld(), rg.getx(), higthest, rg.getZ() - 1), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);

                            locationToRemove.add(new Location(p.getWorld(), rg.getx(), higthest, rg.getZ()));
                            locationToRemove.add(new Location(p.getWorld(), rg.getx() + 1, higthest, rg.getZ()));
                            locationToRemove.add(new Location(p.getWorld(), rg.getx(), higthest, rg.getZ() - 1));

                            p.sendBlockChange(new Location(p.getWorld(), rg.getX(), higthest, rg.getZ()), Material.SEA_LANTERN, (byte) 3);
                            p.sendBlockChange(new Location(p.getWorld(), rg.getX() - 1, higthest, rg.getZ()), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
                            p.sendBlockChange(new Location(p.getWorld(), rg.getX(), higthest, rg.getZ() - 1), Material.LIGHT_BLUE_STAINED_GLASS, (byte) 3);

                            locationToRemove.add(new Location(p.getWorld(), rg.getX(), higthest, rg.getZ()));
                            locationToRemove.add(new Location(p.getWorld(), rg.getX() - 1, higthest, rg.getZ()));
                            locationToRemove.add(new Location(p.getWorld(), rg.getX(), higthest, rg.getZ() - 1));

                            locations.add(locationToRemove);
                        }
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (ArrayList<Location> locs : locations) {
                                for (Location loc : locs) {
                                    Block b = p.getWorld().getBlockAt(loc);
                                    p.sendBlockChange(loc, b.getType(), b.getData());
                                }
                            }
                        }
                    }.runTaskLater(Main.a, 600);
                } else if (args[0].equalsIgnoreCase("setAdmin")) {
                    if (p.hasPermission("nrg.region.setadmin")) {
                        if (args.length >= 2) {
                            Region rg = Region.getRegion(args[1]);
                            if (rg != null) {
                                rg.setAdmin(!rg.isAdmin());
                                if (rg.isAdmin())
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.setAdminTrue").replace("{rgName}", args[1])));
                                else
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.setAdminFalse").replace("{rgName}", args[1])));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("movehere")) {
                    if (Main.pos1.containsKey(p)) {
                        if (Main.pos2.containsKey(p)) {
                            if (args.length >= 2) {
                                Region rg = Region.getRegion(args[1]);
                                if (rg != null) {
                                    if (rg.getCreator().equals(p.getName())) {
                                        boolean isAdmin = false;
                                        boolean isCuboid = false;
                                        if (args.length > 3) if (p.hasPermission("rg.create.admin"))
                                            isAdmin = args[3].equalsIgnoreCase("admin");
                                        if (args.length > 2) if (p.hasPermission("rg.create.cuboid"))
                                            isCuboid = args[2].equalsIgnoreCase("cuboid");
                                        int rgs = 0;
                                        for (Region rg1 : Region.getRegions()) {
                                            if (rg1.getCreator().equals(p.getName())) if (!rg1.isAdmin()) rgs++;
                                        }
                                        if (rgs < PlayerGroup.getGroup(p).getMaxRegionAmount() || isAdmin) {
                                            String[] cords1 = Main.pos1.get(p).split(",");
                                            String[] cords2 = Main.pos2.get(p).split(",");
                                            int x1 = Integer.parseInt(cords1[0]);
                                            int x2 = Integer.parseInt(cords2[0]);
                                            int y1 = Integer.parseInt(cords1[1]);
                                            int y2 = Integer.parseInt(cords2[1]);
                                            int z1 = Integer.parseInt(cords1[2]);
                                            int z2 = Integer.parseInt(cords2[2]);
                                            if (x1 > x2) {
                                                x2 = x1;
                                                x1 = Integer.parseInt(cords2[0]);
                                            }
                                            if (y1 > y2) {
                                                y2 = y1;
                                                y1 = Integer.parseInt(cords2[1]);
                                            }
                                            if (z1 > z2) {
                                                z2 = z1;
                                                z1 = Integer.parseInt(cords2[2]);
                                            }
                                            int area = ((x2 - x1) + 1) * ((z2 - z1) + 1);
                                            int i = 0;
                                            if (area <= PlayerGroup.getGroup(p).getMaxRegionArea() || isAdmin) {
                                                ArrayList<Region> regions = new ArrayList<>(Region.getRegions());
                                                if (!isAdmin) if (isCuboid) {
                                                    for (int x = x1; x <= x2; x++) {
                                                        for (int z = z1; z <= z2; z++) {
                                                            for (Region reg : Region.getRegions(regions, p.getWorld().getName(), x, z)) {
                                                                i++;
                                                                if (!reg.getCreator().equals(p.getName())) {
                                                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.intersecting").replace("{cords}", "x = " + x + ", z = " + z)));
                                                                    return true;
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else for (int x = x1; x <= x2; x++) {
                                                    for (int z = z1; z <= z2; z++) {
                                                        for (int y = y1; y <= y2; y++) {
                                                            for (Region reg : Region.getRegions(regions, p.getWorld().getName(), x, z)) {
                                                                i++;
                                                                if (!reg.getCreator().equals(p.getName())) {
                                                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.intersecting").replace("{cords}", "x = " + x + ", z = " + z)));
                                                                    return true;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                sender.sendMessage("Regions checked: " + i);
                                                rg.setx(x1);
                                                rg.setX(x2);
                                                rg.setz(z1);
                                                rg.setZ(z2);
                                                if (isCuboid) rg.setCuboid(y1, y2);
                                                rg.setAdmin(isAdmin);
                                                rg.save();
                                                Main.playersCreating.remove(p.getName());
                                                Main.pos1.remove(p);
                                                Main.pos2.remove(p);
                                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.rg-created").replace("{rgName}", args[1]).replace("{area}", "" + area)));
                                            }
                                        } else
                                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.to-big-area")));
                                    } else
                                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.to-many-rg")));
                                } else
                                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.rg-inexisting")));
                            } else
                                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Usage.create")));
                        } else
                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.no-sellection")));
                    } else
                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.no-sellection")));
                } else if (args[0].equalsIgnoreCase("give")) {
                    Region rg = Region.getRegion(args[1]);
                    if (rg != null) {
                        if (rg.getCreator().equals(p.getName()) || p.hasPermission("nrg.region.creator")) {
                            rg.setCreator(args[2]);
                            rg.save();
                            Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.rg-given").replace("{rgName}", args[1]).replace("{player}", "" + args[2])));
                        }
                    } else
                        Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Errors.rg-inexisting")));
                } else
                    Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Usage.rg")));
            } else
                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Usage.rg")));
        }
        return true;
    }
}