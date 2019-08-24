package com.flemmli97.mobbattle.items.entitymanager;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAITeamTarget extends TargetGoal
{
    protected LivingEntity targetEntity;
    private Predicate<LivingEntity> pred;
    public EntityAITeamTarget(CreatureEntity creature, boolean checkSight, boolean onlyNearby)
    {
        super(creature, checkSight, onlyNearby);
        this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
        this.pred = new Predicate<LivingEntity>()
        {
        	@Override
            public boolean apply(@Nullable LivingEntity living)
            {
            		if(living == null)
            			return false;
            		if(living instanceof PlayerEntity && ((PlayerEntity)living).abilities.disableDamage)
            			return false;
                return !Team.isOnSameTeam(living, goalOwner);
            }
        };
    }

    @Override
    public boolean shouldExecute()
    {
		if (this.goalOwner.getRNG().nextInt(5) != 0)
        {
            return false;
        }
        else if(this.goalOwner.getTeam()!=null)
        {
            List<LivingEntity> list = this.goalOwner.world.<LivingEntity>getEntitiesWithinAABB(LivingEntity.class, this.getTargetableArea(this.getTargetDistance()*2), this.pred);
    			list.remove(this.goalOwner);	
            if (list.isEmpty())
                return false;
            else
            {
                this.targetEntity = this.getRandEntList(list);
                return true;
            }
        }
        return false;
    }
    
    private LivingEntity getRandEntList(List<LivingEntity> list)
    {
		Entity living = list.get(this.goalOwner.world.rand.nextInt(list.size()));
		return (LivingEntity) living;
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance)
    {
        return this.goalOwner.getBoundingBox().grow(targetDistance, 4.0D, targetDistance);
    }

    @Override
    public void startExecuting()
    {
        this.goalOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
}
