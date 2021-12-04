package io.github.flemmli97.mobbattle.handler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class EntityAITeamTarget extends TargetGoal {

    protected LivingEntity targetEntity;
    private final Predicate<LivingEntity> pred;

    public EntityAITeamTarget(Mob creature, boolean checkSight, boolean onlyNearby) {
        super(creature, checkSight, onlyNearby);
        this.setFlags(EnumSet.of(Flag.TARGET));
        this.pred = (living) -> {
            if (living == null)
                return false;
            if (living instanceof Player && ((Player) living).getAbilities().invulnerable)
                return false;
            return !Utils.isOnSameTeam(living, this.mob);
        };
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(5) != 0) {
            return false;
        } else if (this.mob.getTeam() != null) {
            List<LivingEntity> list = this.mob.level.getEntitiesOfClass(LivingEntity.class,
                    this.getTargetableArea(this.getFollowDistance() * 2), this.pred);
            list.remove(this.mob);
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
        return list.get(this.mob.level.random.nextInt(list.size()));
    }

    protected AABB getTargetableArea(double targetDistance) {
        return this.mob.getBoundingBox().inflate(targetDistance, 4.0D, targetDistance);
    }

    @Override
    public void start() {
        Utils.setAttackTarget(this.mob, this.targetEntity, false);
        super.start();
    }
}
