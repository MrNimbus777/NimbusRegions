package ru.nimbus.regions.events.player;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.nimbus.regions.Main;
import ru.nimbus.regions.Message;

public class PlayerInteractEvents implements Listener {
    boolean sleep = false;

    @EventHandler
    public void PIE(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        String locale = Message.getLocale(p);
        if (p.getEquipment().getItemInMainHand().getType() == Main.selection_tool || Main.playersCreating.contains(p.getName())) {
            Block b = e.getClickedBlock();
            int x = b.getX();
            int z = b.getZ();
            int y = b.getY();
            String cords = "x = " + x + " z = " + z;
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (Main.pos2.containsKey(p)) {
                    if (!Main.pos2.get(p).split(",")[3].equals(b.getWorld().getName())) {
                        Main.pos2.remove(p);
                    } else {
                        cords = cords + " area: " + (Math.abs((Math.abs(x - Integer.parseInt(Main.pos2.get(p).split(",")[0]))+ 1) * (Math.abs(z - Integer.parseInt(Main.pos2.get(p).split(",")[1])) + 1)));
                    }
                }
                Main.pos1.put(p, x + "," + z + "," + y + "," + b.getWorld().getName());
                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.sell-pos1").replace("{cords}", cords)));
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK && sleep) {
                sleep = false;
                if (Main.pos1.containsKey(p)) {
                    if (!Main.pos1.get(p).split(",")[3].equals(b.getWorld().getName())) {
                        Main.pos1.remove(p);
                    } else {
                        cords = cords + " area: " + (Math.abs((Math.abs(x - Integer.parseInt(Main.pos1.get(p).split(",")[0]))+ 1) * (Math.abs(z - Integer.parseInt(Main.pos1.get(p).split(",")[1])) + 1)));
                    }
                }
                Main.pos2.put(p, x + "," + z + "," + y + "," + b.getWorld().getName());
                Message.send(p, Message.prefix(Main.a.getConfig().getString("Messages." + locale + ".Regions.Success.sell-pos2").replace("{cords}", cords)));
            } else sleep = true;
            e.setCancelled(true);
        }
    }
}