package io.github.flemmli97.mobbattle.mixin;

import io.github.flemmli97.mobbattle.handler.Utils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingMixin {

    @Inject(method = "die", at = @At("RETURN"))
    private void handleDeath(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player)
            return;
        LivingEntity livingEntity = self.getKillCredit();
        if (livingEntity != null) {
            String string = self.getScoreboardName();
            String string2 = livingEntity.getScoreboardName();
            Utils.handleTeamKill(self.level.getScoreboard(), string, string2, ObjectiveCriteria.TEAM_KILL);
            Utils.handleTeamKill(self.level.getScoreboard(), string2, string, ObjectiveCriteria.KILLED_BY_TEAM);
        }
    }
}
