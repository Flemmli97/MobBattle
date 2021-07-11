package com.flemmli97.mobbattle.items.entitymanager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class EntityAITeamTarget extends TargetGoal {

    protected LivingEntity targetEntity;
    private final Predicate<LivingEntity> pred;

    public EntityAITeamTarget(MobEntity creature, boolean checkSight, boolean onlyNearby) {
        super(creature, checkSight, onlyNearby);
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.pred = (living) -> {
            if (living == null)
                return false;
            if (living instanceof PlayerEntity && ((PlayerEntity) living).abilities.disableDamage)
                return false;
            return !Utils.isOnSameTeam(living, this.goalOwner);
        };
    }

    @Override
    public boolean shouldExecute() {
        if (this.goalOwner.getRNG().nextInt(5) != 0) {
            return false;
        } else if (this.goalOwner.getTeam() != null) {
            List<LivingEntity> list = this.goalOwner.world.getEntitiesWithinAABB(LivingEntity.class,
                    this.getTargetableArea(this.getTargetDistance() * 2), this.pred);
            list.remove(this.goalOwner);
            if (list.isEmpty())
                return false;
            else {
                this.targetEntity = this.getRandEntList(list);
                return true;
            }
        }
        return false;
    }

    private LivingEntity getRandEntList(List<LivingEntity> list) {
        return list.get(this.goalOwner.world.rand.nextInt(list.size()));
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        return this.goalOwner.getBoundingBox().grow(targetDistance, 4.0D, targetDistance);
    }

    @Override
    public void startExecuting() {
        Utils.setAttackTarget(this.goalOwner, this.targetEntity, false);
        super.startExecuting();
    }
}
