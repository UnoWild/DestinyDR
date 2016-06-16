package net.dungeonrealms.game.commands;

import net.dungeonrealms.game.commands.generic.BasicCommand;
import net.dungeonrealms.game.mechanics.DungeonManager;
import net.dungeonrealms.game.player.rank.Rank;
import net.dungeonrealms.game.world.party.Affair;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Created by Nick on 10/20/2015.
 */
public class CommandInvoke extends BasicCommand {
    public CommandInvoke(String command, String usage, String description) {
        super(command, usage, description);
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String string, String[] args) {

        if (s instanceof ConsoleCommandSender) return false;

        Player player = (Player) s;
        if (!Rank.isGM(player)) {
            return false;
        }
        if (args.length > 0) {
            if (DungeonManager.getInstance().canCreateInstance()) {
                switch (args[0].toLowerCase()) {
                    case "bandittrove":
                    case "t1dungeon":
                        if (Affair.getInstance().isInParty(player)) {
                            List<Player> list = Affair.getInstance().getParty(player).get().getMembers();
                            list.add(Affair.getInstance().getParty(player).get().getOwner());
                            DungeonManager.getInstance().createNewInstance(DungeonManager.DungeonType.BANDIT_TROVE, list, "T1Dungeon");
                        } else {
                            DungeonManager.getInstance().createNewInstance(DungeonManager.DungeonType.BANDIT_TROVE, Collections.singletonList(player), "T1Dungeon");
                        }
                        break;
                    case "varenglade":
                    case "dodungeon":
                        if (Affair.getInstance().isInParty(player)) {
                            List<Player> list = Affair.getInstance().getParty(player).get().getMembers();
                            list.add(Affair.getInstance().getParty(player).get().getOwner());
                            DungeonManager.getInstance().createNewInstance(DungeonManager.DungeonType.VARENGLADE, list, "DODungeon");
                        } else {
                            DungeonManager.getInstance().createNewInstance(DungeonManager.DungeonType.VARENGLADE, Collections.singletonList(player), "DODungeon");
                        }
                        break;
                    case "infernal_abyss":
                    case "infernalabyss":
                    case "fireydungeon":
                        if (Affair.getInstance().isInParty(player)) {
                            List<Player> list = Affair.getInstance().getParty(player).get().getMembers();
                            list.add(Affair.getInstance().getParty(player).get().getOwner());
                            DungeonManager.getInstance().createNewInstance(DungeonManager.DungeonType.THE_INFERNAL_ABYSS, list, "fireydungeon");
                        } else {
                            DungeonManager.getInstance().createNewInstance(DungeonManager.DungeonType.THE_INFERNAL_ABYSS, Collections.singletonList(player), "fireydungeon");
                        }
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "Unknown instance " + args[0] + "!");
                        break;
                }
            } else {
                player.sendMessage(ChatColor.RED + "There are no dungeons available at this time.");
            }
        }

        return false;
    }
}
