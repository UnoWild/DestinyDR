package net.dungeonrealms.game.commands;

import net.dungeonrealms.API;
import net.dungeonrealms.game.mastery.GamePlayer;
import net.dungeonrealms.game.player.banks.BankMechanics;
import net.dungeonrealms.game.commands.generic.BasicCommand;
import net.dungeonrealms.game.donate.DonationEffects;
import net.dungeonrealms.game.guild.Guild;
import net.dungeonrealms.game.world.items.EnumItem;
import net.dungeonrealms.game.world.items.Item;
import net.dungeonrealms.game.world.items.Item.ItemModifier;
import net.dungeonrealms.game.world.items.Item.ItemTier;
import net.dungeonrealms.game.world.items.Item.ItemType;
import net.dungeonrealms.game.world.items.ItemGenerator;
import net.dungeonrealms.game.world.items.NamedItems;
import net.dungeonrealms.game.world.items.armor.ArmorGenerator;
import net.dungeonrealms.game.world.items.armor.Armor.ArmorTier;
import net.dungeonrealms.game.world.items.repairing.RepairAPI;
import net.dungeonrealms.game.mastery.RealmManager;
import net.dungeonrealms.game.mechanics.ItemManager;
import net.dungeonrealms.game.mechanics.ParticleAPI;
import net.dungeonrealms.game.world.glyph.Glyph;
import net.dungeonrealms.game.world.party.Affair;
import net.dungeonrealms.game.world.realms.Instance;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

/**
 * Created by Nick on 9/17/2015.
 */
public class CommandAdd extends BasicCommand {

    public CommandAdd(String command, String usage, String description) {
        super(command, usage, description);
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String string, String[] args) {
        if (s instanceof ConsoleCommandSender) return false;
        Player player = (Player) s;
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "[WARNING] " + ChatColor.YELLOW + "You do not have permissions for this!");
            return false;
        }
        if (args.length > 0) {
            int tier;
            switch (args[0]) {
                case "pcheck":
                    player.sendMessage(ChatColor.GREEN + "There are " + String.valueOf(Affair.getInstance()._parties.size()));
                    break;
                case "check":
                    player.sendMessage("YOUR REALM EXIST? " + String.valueOf(Instance.getInstance().doesRemoteRealmExist(player.getUniqueId().toString())));
                    break;
                case "uuid":
                    player.sendMessage(Bukkit.getPlayer(API.getUUIDFromName(player.getName())).getDisplayName());
                    break;
                case "name":
                    player.sendMessage(API.getNameFromUUID(player.getUniqueId()));
                    break;
                case "ga":
                    player.getInventory().addItem(Glyph.getInstance().getBaseGlyph(args[1], Integer.valueOf(args[2]), Glyph.GlyphType.ARMOR));
                    break;
                case "gw":
                    player.getInventory().addItem(Glyph.getInstance().getBaseGlyph(args[1], Integer.valueOf(args[2]), Glyph.GlyphType.WEAPON));
                    break;
                case "uploadrealm":
                    new RealmManager().uploadRealm(player.getUniqueId());
                    break;
                case "realm":
                    Instance.getInstance().openRealm(player);
                    //new RealmManager().downloadRealm(player.getUniqueId());
                    break;
                case "weapon":
                    tier = Integer.parseInt(args[1]);
                    player.getInventory().addItem(new ItemGenerator().getDefinedStack(ItemGenerator.getRandomItemType(), ItemTier.getByTier(tier), ItemGenerator.getRandomItemModifier()));
                    break;
                case "armor":
                    tier = Integer.parseInt(args[1]);
                    player.getInventory().addItem(new ArmorGenerator().getDefinedStack(ArmorGenerator.getRandomEquipmentType(), ArmorTier.getByTier(tier), ArmorGenerator.getRandomItemModifier()));
                    break;
                case "particle":
                    if (args[1] != null)
                        ParticleAPI.sendParticleToLocation(ParticleAPI.ParticleEffect.getById(Integer.valueOf(args[1])), player.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 250);
                    break;
                case "bank":
                    net.minecraft.server.v1_8_R3.ItemStack nmsBank = CraftItemStack.asNMSCopy(new ItemStack(Material.ENDER_CHEST));
                    NBTTagCompound Banktag = nmsBank.getTag() == null ? new NBTTagCompound() : nmsBank.getTag();
                    Banktag.set("type", new NBTTagString("bank"));
                    nmsBank.setTag(Banktag);
                    player.getInventory().addItem(CraftItemStack.asBukkitCopy(nmsBank));
                    break;
                case "trail":
                    if (args[1] != null)
                        DonationEffects.getInstance().PLAYER_PARTICLE_EFFECTS.put(player, ParticleAPI.ParticleEffect.getById(Integer.valueOf(args[1])));
                    break;
                case "gold":
                    DonationEffects.getInstance().PLAYER_GOLD_BLOCK_TRAILS.add(player);
                    break;
                case "pick":
                    tier = 1;
                    if (args.length == 2)
                        tier = Integer.parseInt(args[1]);
                    player.getInventory().addItem(ItemManager.createPickaxe(tier));
                    break;
                case "rod":
                    int rodTier = 1;
                    if (args.length == 2)
                        rodTier = Integer.parseInt(args[1]);
                    player.getInventory().addItem(ItemManager.createFishingPole(rodTier));
                    break;
                case "resetbook":
                    player.getInventory().addItem(ItemManager.createItem(EnumItem.RetrainingBook));
                    break;
                case "journal":
                    player.getInventory().addItem(ItemManager.createCharacterJournal(player));
                    break;
                case "scrap":
                    player.getInventory().addItem(ItemManager.createArmorScrap(1));
                    player.getInventory().addItem(ItemManager.createArmorScrap(2));
                    player.getInventory().addItem(ItemManager.createArmorScrap(3));
                    player.getInventory().addItem(ItemManager.createArmorScrap(4));
                    player.getInventory().addItem(ItemManager.createArmorScrap(5));
                    break;
                case "potion":
                    player.getInventory().addItem(ItemManager.createHealthPotion(1, false, false));
                    player.getInventory().addItem(ItemManager.createHealthPotion(2, false, false));
                    player.getInventory().addItem(ItemManager.createHealthPotion(3, false, false));
                    player.getInventory().addItem(ItemManager.createHealthPotion(4, false, false));
                    player.getInventory().addItem(ItemManager.createHealthPotion(5, false, false));
                    player.getInventory().addItem(ItemManager.createHealthPotion(1, false, true));
                    player.getInventory().addItem(ItemManager.createHealthPotion(2, false, true));
                    player.getInventory().addItem(ItemManager.createHealthPotion(3, false, true));
                    player.getInventory().addItem(ItemManager.createHealthPotion(4, false, true));
                    player.getInventory().addItem(ItemManager.createHealthPotion(5, false, true));
                    break;
                case "food":
                    player.setFoodLevel(1);
                    player.getInventory().addItem(ItemManager.createHealingFood(1, Item.ItemModifier.COMMON));
                    player.getInventory().addItem(ItemManager.createHealingFood(1, Item.ItemModifier.RARE));
                    player.getInventory().addItem(ItemManager.createHealingFood(1, Item.ItemModifier.LEGENDARY));
                    player.getInventory().addItem(ItemManager.createHealingFood(2, Item.ItemModifier.COMMON));
                    player.getInventory().addItem(ItemManager.createHealingFood(2, Item.ItemModifier.RARE));
                    player.getInventory().addItem(ItemManager.createHealingFood(2, Item.ItemModifier.LEGENDARY));
                    player.getInventory().addItem(ItemManager.createHealingFood(3, Item.ItemModifier.COMMON));
                    player.getInventory().addItem(ItemManager.createHealingFood(3, Item.ItemModifier.RARE));
                    player.getInventory().addItem(ItemManager.createHealingFood(3, Item.ItemModifier.LEGENDARY));
                    player.getInventory().addItem(ItemManager.createHealingFood(4, Item.ItemModifier.COMMON));
                    player.getInventory().addItem(ItemManager.createHealingFood(4, Item.ItemModifier.RARE));
                    player.getInventory().addItem(ItemManager.createHealingFood(4, Item.ItemModifier.LEGENDARY));
                    player.getInventory().addItem(ItemManager.createHealingFood(5, Item.ItemModifier.COMMON));
                    player.getInventory().addItem(ItemManager.createHealingFood(5, Item.ItemModifier.RARE));
                    player.getInventory().addItem(ItemManager.createHealingFood(5, Item.ItemModifier.LEGENDARY));
                    break;
                case "test":
                    Bukkit.broadcastMessage("Get2" + String.valueOf(RepairAPI.getCustomDurability(player.getItemInHand())));
                    break;
                case "orb":
                    player.getInventory().addItem(ItemManager.createOrbofAlteration());
                    break;
                case "armorenchant":
                    tier = Integer.parseInt(args[1]);
                    player.getInventory().addItem(ItemManager.createArmorEnchant(tier));
                    break;
                case "weaponenchant":
                    tier = Integer.parseInt(args[1]);
                    player.getInventory().addItem(ItemManager.createWeaponEnchant(tier));
                    break;
                case "protectscroll":
                    tier = Integer.parseInt(args[1]);
                    player.getInventory().addItem(ItemManager.createProtectScroll(tier));
                    break;
                case "pouch":
                    tier = Integer.parseInt(args[1]);
                    player.getInventory().addItem(BankMechanics.getInstance().createGemPouch(tier, 0));
                    break;
                case "votemessage":
                    if (API.getGamePlayer(player) == null) {
                        break;
                    }
                    GamePlayer gamePlayer = API.getGamePlayer(player);
                    int expToLevel = gamePlayer.getEXPNeeded(gamePlayer.getLevel());
                    int expToGive = expToLevel / 20;
                    expToGive += 100;
                    gamePlayer.addExperience(expToGive, false);
                    TextComponent bungeeMessage = new TextComponent(ChatColor.AQUA.toString() + ChatColor.BOLD + ChatColor.UNDERLINE + "HERE");
                    bungeeMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://minecraftservers.org/server/298658"));
                    bungeeMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to vote!").create()));
                    TextComponent test = new TextComponent(ChatColor.AQUA + player.getName() + ChatColor.RESET + ChatColor.GRAY + " voted for 15 ECASH & 5% EXP @ vote ");
                    test.addExtra(bungeeMessage);
                    Bukkit.spigot().broadcast(test);
                    break;
                case "blayshan":
                	player.getInventory().addItem(NamedItems.blayshanAxe);
                	break;
                case "xwaffle":
                	ItemStack xwaffleItem = new ItemGenerator().getDefinedStack(ItemType.AXE, ItemTier.TIER_5, ItemModifier.LEGENDARY);
                	ItemMeta meta = xwaffleItem.getItemMeta();
                	meta.setDisplayName(ChatColor.YELLOW + "Xwaffle's Cock");
                	List<String> lore = meta.getLore();
                	int i = 0;
                	for(String str : lore){
                		if(str.startsWith("DMG: ")){
                			lore.set(i, ChatColor.RED + "DMG: " + 1000);
                			lore.add("Xwaffle is the best.");
                			break;	
                		}
                		i++;
                	}
                	xwaffleItem.setItemMeta(meta);
                	net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(xwaffleItem);
                	nms.getTag().setInt("damage", 1000);
                	player.getInventory().addItem(CraftItemStack.asBukkitCopy(nms));
                	break;
            }
        }

        return true;
    }
}
