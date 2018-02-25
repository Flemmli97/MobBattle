package com.flemmli97.mobbattle.items.entityManager;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAITeamTarget extends EntityAITarget
{
    protected EntityLiving targetEntity;
    private Predicate<EntityLiving> pred;
    public EntityAITeamTarget(EntityCreature creature, boolean checkSight, boolean onlyNearby)
    {
        super(creature, checkSight, onlyNearby);
        this.setMutexBits(1);
        this.pred = new Predicate<EntityLiving>()
        {
            public boolean apply(@Nullable EntityLiving living)
            {
                return living == null ? false : Team.isOppositeTeam(taskOwner, living);
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
            List<EntityLiving> list = this.taskOwner.world.<EntityLiving>getEntitiesWithinAABB(EntityLiving.class, this.getTargetableArea(this.getTargetDistance()*2), this.pred);
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
    
    private EntityLiving getRandEntList(List<EntityLiving> list)
    {
    		Entity living = list.get(this.taskOwner.world.rand.nextInt(list.size()));
    		return (EntityLiving) living;
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance)
    {
        return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 4.0D, targetDistance);
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