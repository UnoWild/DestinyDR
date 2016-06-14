package net.dungeonrealms.game.world.entities.types.monsters.BowMobs;

import net.dungeonrealms.API;
import net.dungeonrealms.game.miscellaneous.SkullTextures;
import net.dungeonrealms.game.world.anticheat.AntiCheat;
import net.dungeonrealms.game.world.entities.EnumEntityType;
import net.dungeonrealms.game.world.entities.types.monsters.EnumMonster;
import net.dungeonrealms.game.world.entities.types.monsters.base.DRSkeleton;
import net.dungeonrealms.game.world.items.DamageAPI;
import net.dungeonrealms.game.world.items.Item;
import net.dungeonrealms.game.world.items.itemgenerator.ItemGenerator;
import net.minecraft.server.v1_9_R2.EntityLiving;
import net.minecraft.server.v1_9_R2.EnumItemSlot;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import net.minecraft.server.v1_9_R2.World;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Kieran Quigley (Proxying) on 14-Jun-16.
 */
public class RangedSkeleton extends DRSkeleton {
    private int tier;

    public RangedSkeleton(World world, EnumMonster monsterType ,EnumEntityType entityType, int tier) {
        super(world, monsterType, tier, entityType);
        this.entityType = entityType;
        this.tier = tier;
        LivingEntity livingEntity = (LivingEntity) this.getBukkitEntity();
        this.setEquipment(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(SkullTextures.SKELETON.getSkull()));
        livingEntity.getEquipment().setHelmet(SkullTextures.SKELETON.getSkull());
        switch (monsterType) {
            case Pirate:
            case MayelPirate:
                this.setEquipment(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(SkullTextures.PIRATE.getSkull()));
                livingEntity.getEquipment().setHelmet(SkullTextures.PIRATE.getSkull());
                break;
            case FrozenSkeleton:
                this.setEquipment(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(SkullTextures.FROZEN_SKELETON.getSkull()));
                livingEntity.getEquipment().setHelmet(SkullTextures.FROZEN_SKELETON.getSkull());
                break;
        }
    }

    public RangedSkeleton(World world) {
        super(world);
    }

    @Override
    public void setWeapon(int tier) {
        ItemStack weapon = getTierWeapon(tier);
        this.setEquipment(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(weapon));
        LivingEntity livingEntity = (LivingEntity) this.getBukkitEntity();
        livingEntity.getEquipment().setItemInMainHand(weapon);
    }

    private ItemStack getTierWeapon(int tier) {
        ItemStack item = new ItemGenerator().setType(Item.ItemType.BOW).setRarity(API.getItemRarity(false)).setTier(Item.ItemTier.getByTier(tier)).generateItem().getItem();
        AntiCheat.getInstance().applyAntiDupe(item);
        return item;
    }


    @Override
    public void a(EntityLiving entityliving, float f) {
        net.minecraft.server.v1_9_R2.ItemStack nmsItem = this.getEquipment(EnumItemSlot.MAINHAND);
        NBTTagCompound tag = nmsItem.getTag();
        DamageAPI.fireArrowFromMob((CraftLivingEntity) this.getBukkitEntity(), tag, (CraftLivingEntity) entityliving.getBukkitEntity());
    }

    @Override
    public void setStats() {

    }

    @Override
    public EnumMonster getEnum() {
        return this.monsterType;
    }
}
