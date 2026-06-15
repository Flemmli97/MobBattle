package io.github.flemmli97.mobbattle.common.entity.goal;

import io.github.flemmli97.mobbattle.common.utils.ActiveTargetMobbattle;
import io.github.flemmli97.mobbattle.common.utils.Utils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public class TeamTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

    public TeamTargetGoal(Mob mob, boolean checkSight, boolean onlyNearby) {
        super(mob, LivingEntity.class, 10, checkSight, onlyNearby, targetPred(mob));
        this.targetConditions.ignoreLineOfSight();
    }

    public static Predicate<LivingEntity> targetPred(Mob mob) {
        return living -> {
            if (living instanceof Player player && player.getAbilities().invulnerable)
                return false;
            return Utils.canTargetEntity(living, mob);
        };
    }

    @Override
    public boolean canUse() {
        if (this.mob instanceof ActiveTargetMobbattle active)
            active.mobbattle$setTargeting(true);
        boolean res = super.canUse();
        if (this.mob instanceof ActiveTargetMobbattle active)
            active.mobbattle$setTargeting(false);
        return res;
    }

    @Override
    public void start() {
        Utils.setAttackTarget(this.mob, this.target, false);
        super.start();
    }
}
