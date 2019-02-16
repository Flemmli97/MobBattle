package com.flemmli97.mobbattle.items.entitymanager;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAITeamTarget extends EntityAITarget
{
    protected EntityLivingBase targetEntity;
    private Predicate<EntityLivingBase> pred;
    public EntityAITeamTarget(EntityCreature creature, boolean checkSight, boolean onlyNearby)
    {
        super(creature, checkSight, onlyNearby);
        this.setMutexBits(1);
        this.pred = new Predicate<EntityLivingBase>()
        {
        	@Override
            public boolean apply(@Nullable EntityLivingBase living)
            {
            		if(living == null)
            			return false;
            		if(living instanceof EntityPlayer && ((EntityPlayer)living).abilities.disableDamage)
            			return false;
                return !Team.isOnSameTeam(living, taskOwner);
            }
        };
    }

    @Override
    public boolean shouldExecute()
    {
    		if (this.taskOwner.getRNG().nextInt(5) != 0)
        {
            return false;
        }
        else if(this.taskOwner.getTeam()!=null)
        {
            List<EntityLivingBase> list = this.taskOwner.world.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, this.getTargetableArea(this.getTargetDistance()*2), this.pred);
    			list.remove(this.taskOwner);
    			
            if (list.isEmpty())
            {
                return false;
            }
            else
            {
                this.targetEntity = this.getRandEntList(list);
                return true;
            }
        }
        return false;
    }
    
    private EntityLivingBase getRandEntList(List<EntityLivingBase> list)
    {
		Entity living = list.get(this.taskOwner.world.rand.nextInt(list.size()));
		return (EntityLivingBase) living;
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance)
    {
        return this.taskOwner.getBoundingBox().grow(targetDistance, 4.0D, targetDistance);
    }

    @Override
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
}