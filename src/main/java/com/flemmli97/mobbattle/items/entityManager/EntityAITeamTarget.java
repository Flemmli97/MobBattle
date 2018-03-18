package com.flemmli97.mobbattle.items.entityManager;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

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
            public boolean apply(@Nullable EntityLivingBase living)
            {
	        		if(living == null)
	        			return false;
	        		if(living instanceof EntityPlayer && ((EntityPlayer)living).capabilities.isCreativeMode)
	        			return false;
	            return Team.isOppositeTeam(taskOwner, living);
            }
        };
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
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
    		EntityLivingBase living = list.get(this.taskOwner.world.rand.nextInt(list.size()));
    		return living;
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance)
    {
        return this.taskOwner.getEntityBoundingBox().expand(targetDistance, 4.0D, targetDistance);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
}