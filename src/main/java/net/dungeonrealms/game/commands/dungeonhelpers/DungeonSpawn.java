package net.dungeonrealms.game.commands.dungeonhelpers;

import net.dungeonrealms.API;
import net.dungeonrealms.DungeonRealms;
import net.dungeonrealms.game.commands.generic.BasicCommand;
import net.dungeonrealms.game.mastery.MetadataUtils;
import net.dungeonrealms.game.mastery.Utils;
import net.dungeonrealms.game.player.rank.Rank;
import net.dungeonrealms.game.world.entities.EnumEntityType;
import net.dungeonrealms.game.world.entities.types.monsters.BowMobs.RangedSkeleton;
import net.dungeonrealms.game.world.entities.types.monsters.EnumMonster;
import net.dungeonrealms.game.world.entities.types.monsters.MeleeMobs.EntityBandit;
import net.dungeonrealms.game.world.entities.types.monsters.MeleeMobs.EntityPirate;
import net.dungeonrealms.game.world.entities.types.monsters.MeleeMobs.LargeSpider;
import net.dungeonrealms.game.world.entities.utils.EntityStats;
import net.minecraft.server.v1_9_R2.Entity;
import net.minecraft.server.v1_9_R2.World;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Kieran Quigley (Proxying) on 14-Jun-16.
 */
public class DungeonSpawn extends BasicCommand {
    public DungeonSpawn(String command, String usage, String description) {
        super(command, usage, description);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 2) {
            return false;
        }
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        if (!Rank.isDev(player)) {
            return false;
        }
        int tier = Integer.parseInt(strings[0]);
        World world = ((CraftWorld) player.getWorld()).getHandle();
        Location location = player.getLocation();
        String customName = "";
        if (strings.length == 3) {
            customName = strings[2];
        }
        if (tier == 1) {
            Entity entity = null;
            EnumMonster monster = null;
            switch (strings[1].toLowerCase()) {
                case "pirate":
                    monster = EnumMonster.MayelPirate;
                    entity = new EntityPirate(world, EnumMonster.MayelPirate, 1);
                    break;
                case "rangedpirate":
                    monster = EnumMonster.Pirate;
                    entity = new RangedSkeleton(world, EnumMonster.Pirate, EnumEntityType.HOSTILE_MOB, 1);
                    break;
                case "spider":
                    monster = EnumMonster.Spider1;
                    entity = new LargeSpider(world, 1, EnumMonster.Spider1);
                    break;
                case "bandit":
                    monster = EnumMonster.Bandit;
                    entity = new EntityBandit(world, 1, EnumEntityType.HOSTILE_MOB, EnumMonster.MayelPirate);
                    break;
            }
            if (entity != null) {
                LivingEntity livingEntity = (LivingEntity) entity.getBukkitEntity();
                livingEntity.setRemoveWhenFarAway(false);
                entity.setLocation(location.getX(), location.getY(), location.getZ(), 1, 1);
                world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
                registerEntityStats(entity, monster, tier, customName);
            }
            return true;
        }
        return false;
        //dspawn 1 pirate Proxying
    }

    private void registerEntityStats(Entity entity, EnumMonster monsterType, int tier, String customName) {
        int newLevel = Utils.getRandomFromTier(tier, "high");
        MetadataUtils.registerEntityMetadata(entity, EnumEntityType.HOSTILE_MOB, tier, newLevel);
        EntityStats.setMonsterRandomStats(entity, newLevel, tier);
        String newLevelName = ChatColor.LIGHT_PURPLE.toString() + "[" + newLevel + "] ";
        if (customName.equals("")) {
            customName = monsterType.getPrefix() + " " + monsterType.name + " " + monsterType.getSuffix() + " ";
        }
        entity.setCustomName(newLevelName + API.getTierColor(tier) + customName.trim());
        entity.setCustomNameVisible(true);
        entity.getBukkitEntity().setMetadata("customname", new FixedMetadataValue(DungeonRealms.getInstance(), API.getTierColor(tier) + customName.trim()));
    }
}
