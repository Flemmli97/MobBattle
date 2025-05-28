package io.github.flemmli97.mobbattle.handler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public class EntityAITeamTarget extends NearestAttackableTargetGoal<LivingEntity> {

    public EntityAITeamTarget(Mob mob, boolean checkSight, boolean onlyNearby) {
        super(mob, LivingEntity.class, 10, checkSight, onlyNearby, targetPred(mob));
        this.targetConditions.ignoreLineOfSight();
    }

    public static TargetingConditions.Selector targetPred(Mob mob) {
        return (living, level) -> {
            if (living instanceof Player player && player.getAbilities().invulnerable)
                return false;
            return Utils.canTargetEntity(living, mob);
        };
    }

    @Override
    public void start() {
        Utils.setAttackTarget(this.mob, this.target, false);
        super.start();
    }
}
