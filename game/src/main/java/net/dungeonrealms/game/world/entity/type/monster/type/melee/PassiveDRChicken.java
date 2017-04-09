package net.dungeonrealms.game.world.entity.type.monster.type.melee;

import lombok.Getter;
import net.dungeonrealms.game.mastery.AttributeList;
import net.dungeonrealms.game.world.entity.type.monster.DRMonster;
import net.dungeonrealms.game.world.entity.type.monster.type.EnumMonster;
import net.dungeonrealms.game.world.entity.util.pathfinders.PathfinderPassiveMeleeAttack;
import net.minecraft.server.v1_9_R2.*;

public class PassiveDRChicken extends EntityChicken implements DRMonster {
	
    @Getter
    protected AttributeList attributes = new AttributeList();

    public PassiveDRChicken(World world, int tier) {
        super(world);
        setupMonster(tier);
    }

    @Override
    protected void r() {
        this.targetSelector.a(0, new PathfinderPassiveMeleeAttack(this, 1.2D, true));
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
//        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.4D));a
//        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
//        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.0D, false, bE));
//        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 0.9D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }
    
    @Override
    public EnumMonster getEnum() {
        return EnumMonster.PassiveChicken;
    }
    
    public EntityLiving getNMS() {
    	return this;
    }
}
