package net.dungeonrealms.commands;

import net.dungeonrealms.mastery.NBTUtils;
import net.dungeonrealms.monsters.entities.EntityPirate;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

/**
 * Created by Nick on 9/17/2015.
 */
public class CommandSpawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String string, String[] args) {
        if (s instanceof ConsoleCommandSender) return false;
        Player player = (Player) s;
        if (args.length > 0) {
            switch (args[0]) {
                case "wolf":
                    Wolf w = (Wolf) Bukkit.getWorld(player.getWorld().getName()).spawnEntity(player.getLocation(), EntityType.WOLF);
                    NBTUtils.nullifyAI(w);
                    break;
                case "pirate":
                    World world = ((CraftWorld) player.getWorld()).getHandle();
                    EntityPirate zombie = new EntityPirate(world);
                    world.addEntity(zombie);
                    zombie.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
                    break;
                default:
            }
        }
        return true;
    }
}
