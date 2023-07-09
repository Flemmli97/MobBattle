package io.github.flemmli97.mobbattle.handler;

import io.github.flemmli97.mobbattle.MobBattle;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class EntityAIHurt extends TargetGoal {

    protected final TargetingConditions targetConditions;
    private int timestamp;

    public EntityAIHurt(Mob mob) {
        super(mob, true);
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.targetConditions = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting().selector(EntityAITeamTarget.targetPred(mob));
    }

    @Override
    public boolean canUse() {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingEntity = this.mob.getLastHurtByMob();
        if (i == this.timestamp || livingEntity == null) {
            return false;
        }
        return this.canAttack(livingEntity, this.targetConditions);
    }

    @Override
    public boolean canContinueToUse() {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingEntity = this.mob.getLastHurtByMob();
        if (this.mob.getLastHurtMobTimestamp() + 40 < i && this.mob.getTarget() != livingEntity && !livingEntity.getType().is(MobBattle.HURT_IGNORED)) {
            return false;
        }
        return super.canContinueToUse();
    }

    @Override
    public void start() {
        this.mob.setTarget(this.mob.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        this.timestamp = -1;
    }
}

