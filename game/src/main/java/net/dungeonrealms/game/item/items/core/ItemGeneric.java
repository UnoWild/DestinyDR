package net.dungeonrealms.game.item.items.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.dungeonrealms.DungeonRealms;
import net.dungeonrealms.GameAPI;
import net.dungeonrealms.game.anticheat.AntiDuplication;
import net.dungeonrealms.game.enchantments.EnchantmentAPI;
import net.dungeonrealms.game.item.ItemType;
import net.dungeonrealms.game.item.PersistentItem;
import net.minecraft.server.v1_9_R2.NBTTagCompound;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * ItemGeneric - A GearItem that can be applied to any item.
 * Contains generic information that can be applied to any item such as Soulbound, untradeable, etc.
 * 
 * Created March 28th, 2017.
 * @author Kneesnap
 */
public abstract class ItemGeneric extends PersistentItem {

	private Map<ItemData, Boolean> dataMap = new HashMap<>();
	
	private List<String> lore;
	
	@Getter @Setter
	private boolean antiDupe;
	
	@Getter
	private ItemType itemType;
	
	@Getter @Setter //Whether or not this item should be removed.
	private boolean destroyed;
	
	private boolean resetLore; //This marks whether lore should be reset. This is used so lore isn't added from a previous item update.
	
	@Setter @Getter
	private int price; //The price of this item. 0 marks no price.
	
	@Setter @Getter
	private boolean showPrice = true; //Whether or not lore should be created for this price.
	
	private long soulboundTrade = 0;
	private List<String> soulboundAllowedTraders;
	
	@Getter
	private boolean glowing;
	
	@Getter
	private ItemMeta meta = new ItemStack(Material.DIRT).getItemMeta().clone(); //Default ItemMeta
	
	//Tier should share the same name for consistency.
	protected static final String TIER = "itemTier";
	
	public ItemGeneric(ItemType type) {
		this(null, type);
	}
	
	public ItemGeneric(ItemStack item) {
		this(item, getType(item));
	}
	
	public ItemGeneric(ItemStack item, ItemType type) {
		super(item);
		this.itemType = type;
		this.lore = new ArrayList<>();
		this.dataMap = new HashMap<>();
		
		//Alert us if anything goes awry. Attempts to delete the item. (It likely won't)
		if (isEventItem() && !(DungeonRealms.isMaster() || DungeonRealms.isEvent())) {
			GameAPI.sendNetworkMessage("GMMessage", ChatColor.RED + "[WARNING] " + ChatColor.WHITE + "Found event item on non-event shard! Found "
					+ item.getAmount() + "x" + (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name()));
			setDestroyed(true);
		}
	}
	
	public boolean isPermanentUntradeable() {
		return getSData(ItemData.PUNTRADEABLE);
	}
	public boolean isDisplay() {
		return getSData(ItemData.MENU);
	}
	
	public boolean isSoulbound() {
		return getSData(ItemData.SOULBOUND);
	}
	
	public boolean isUndroppable() {
		return getSData(ItemData.UNDROPPABLE);
	}
	
	public boolean isUntradeable() {
		return getSData(ItemData.UNTRADEABLE);
	}
	
	public boolean isEventItem() {
		return getSData(ItemData.EVENT);
	}
	
	public boolean isDungeon() {
		return getSData(ItemData.DUNGEON);
	}
	
	public ItemGeneric setDisplay(boolean display) {
		setData(ItemData.MENU, display);
		return this;
	}
	
	public ItemGeneric setDungeon(boolean dungeon) {
		setData(ItemData.DUNGEON, dungeon);
		return this;
	}
	
	public ItemGeneric setPermUntradeable(boolean perm) {
		setData(ItemData.PUNTRADEABLE, perm);
		return this;
	}
	
	public ItemGeneric setSoulbound(boolean soulbound) {
		setData(ItemData.SOULBOUND, soulbound);
		return this;
	}
	
	public ItemGeneric setUndroppable(boolean undroppable) {
		setData(ItemData.UNDROPPABLE, undroppable);
		return this;
	}
	
	public ItemGeneric setUntradeable(boolean untradeable) {
		setData(ItemData.UNTRADEABLE, untradeable);
		return this;
	}
	
	public ItemGeneric setEventItem(boolean event) {
		setData(ItemData.EVENT, event);
		return this;
	}

	protected ItemGeneric setType(ItemType type) {
		if (type != null)
			this.itemType = type;
		return this;
	}
	
	public ItemGeneric setGlowing(boolean b) {
		this.glowing = b;
		return this;
	}
	
	@Override
	protected void loadItem() {
		if (dataMap == null)
			dataMap = new HashMap<>();
		
		this.meta = getItem().getItemMeta();
		
		for(ItemData data : ItemData.values())
			dataMap.put(data, getTagBool(data.getNBTTag()));
		
		if (isSoulbound() && hasTag("soulboundTrade")) {
			long time = getTag().getLong("soulboundTrade");
			if (time > System.currentTimeMillis()) {
				this.soulboundTrade = time;
				this.soulboundAllowedTraders = Arrays.asList(getTagString("soulboundBypass").split(","));
			}
		}
		
		setPrice(getTagInt("price"));
		setShowPrice(getTagBool("showPrice"));
		setGlowing(EnchantmentAPI.isGlowing(getItem()));
	}
	
	/**
	 * Remove the item unique id. Used by Loot Chests to not produce duplicate items.
	 */
	public void removeEpoch() {
		removeTag("u");
	}
	
	@Override
	public ItemStack getItem() {
		return isDestroyed() ? new ItemStack(Material.AIR) : super.getItem();
	}
	
	@Override
	public ItemStack generateItem() {
		this.meta = getStack().getItemMeta();
		return super.generateItem();
	}

	@Override
	public void updateItem() {
		if (isDestroyed())
			return;
		
		for (ItemData data : ItemData.values()) {
			boolean enabled = getSData(data);
			setTagBool(data.getNBTTag(), enabled);
			
			if(data.getDisplay() != null && enabled)
				addLore(data.getDisplay());
		}
		
		// Don't save this data if it has expired.
		if (isSoulbound() && this.soulboundTrade > 0) {
			getTag().setLong("soulboundTrade", this.soulboundTrade);
			setTagString("soulboundBypass", String.join(",", this.soulboundAllowedTraders));
		}
		
		if (getItemType() != null)
			setTagString("type", getItemType().getNBT());

		//  APPLY ANTI DUPE  //
		if (isAntiDupe() && !hasTag("u"))
			setTagString("u", AntiDuplication.createEpoch(getItem()));
		
		if (getPrice() > 0) {
			setTagInt("price", getPrice());
			if(isShowPrice()) {
				setTagBool("showPrice", true);
				addLore(ChatColor.GREEN + "Price: " + ChatColor.WHITE + getPrice() + "g" + ChatColor.GREEN + " each");
			}
		}
		
		if (isGlowing())
			EnchantmentAPI.addGlow(getItem());
		
		saveMeta();
		resetLore = true;
	}
	
	/**
	 * Saves data in meta to NBT.
	 * Just using setItemMeta will override NBT tags, so we set them manually.
	 */
	private void saveMeta() {
		ItemStack withMeta = getItem().clone();
		getMeta().setLore(this.lore);
		withMeta.setItemMeta(getMeta());
		net.minecraft.server.v1_9_R2.ItemStack nms = CraftItemStack.asNMSCopy(withMeta);
		if (!nms.hasTag())
			return;
		NBTTagCompound merge = nms.getTag();
		for (String key : merge.c())
			getTag().set(key, merge.get(key));
		getItem().setItemMeta(getMeta());
	}
	
	/**
	 * Remove the price from this item.
	 */
	public void removePrice() {
		setPrice(0);
	}
	
	/**
	 * Allows this item to be traded to a specified player for X minutes.
	 * @param Player
	 * @param time (In Seconds)
	 */
	public void addSoulboundBypass(Player p, int time) {
		this.soulboundTrade = System.currentTimeMillis() + time * 1000;
		this.soulboundAllowedTraders.add(p.getName());
	}
	
	/**
	 * Can this item be traded to a specified player?
	 */
	public boolean isSoulboundBypass(Player p) {
		return !isSoulbound() || (this.soulboundTrade > System.currentTimeMillis() && this.soulboundAllowedTraders.contains(p.getName()));
	}
	
	protected void addLore(String s) {
		addLore(s, resetLore);
	}
	
	protected void addLore(String s, boolean reset) {
		if(reset) {
			this.lore.clear();
			resetLore = false;
		}
		this.lore.add(ChatColor.GRAY + s);
	}
	
	protected void removeLore(String startsWith) {
		for(int i = 0; i < this.lore.size(); i++) {
			if (this.lore.get(i).startsWith(startsWith)) {
				this.lore.remove(i);
				return;
			}
		}
	}
	
	protected void clearLore() {
		this.lore.clear();
	}
	
	protected boolean getSData(ItemData data) {
		return dataMap.containsKey(data) && dataMap.get(data);
	}
	
	protected void setData(ItemData data, boolean enabled) {
		dataMap.put(data, enabled);
	}
	
	protected enum ItemData { 
		SOULBOUND(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "Soulbound"),
		UNTRADEABLE(ChatColor.GRAY + "Untradeable"),
		PUNTRADEABLE(ChatColor.GRAY + "Permanent Untradeable"),
		EVENT(ChatColor.RED + "Event Item"),
		MENU(ChatColor.GRAY + "Display Item"),
		DUNGEON(ChatColor.RED + "Dungeon Item"),
		UNDROPPABLE(null);
		
		@Getter
		private final String display;
		
		ItemData(String display) {
			this.display = display;
		}
		
		public String getNBTTag() {
			return this.name().toLowerCase();
		}
	}
}