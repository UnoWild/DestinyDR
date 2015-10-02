package net.dungeonrealms.listeners;

import net.dungeonrealms.anticheat.AntiCheat;
import net.dungeonrealms.mastery.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by Nick on 10/1/2015.
 */
public class AntiCheatListener implements Listener {

    /**
     * This event is used to checking if the player is duplicating
     * an item or item(s).
     *
     * @param event
     * @since 1.0
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(InventoryClickEvent event) {
        if (AntiCheat.getInstance().watchForDupes(event)) {
            Utils.log.warning("[ANTI-CHEAT] [DUPE] Player: " + event.getWhoClicked().getName());
            Bukkit.broadcastMessage(ChatColor.RED + "[AntiCheat]" + ChatColor.BLUE + " Type:" + ChatColor.AQUA + "Duplication " + ChatColor.BLUE + "User:" + ChatColor.AQUA + event.getWhoClicked().getName() + ChatColor.BLUE + " Time:" + ChatColor.AQUA + System.currentTimeMillis());
        }
    }

}
