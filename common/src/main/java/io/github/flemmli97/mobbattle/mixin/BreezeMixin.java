package io.github.flemmli97.mobbattle.mixin;

import io.github.flemmli97.mobbattle.handler.SetActiveTargetMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.breeze.Breeze;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Breeze.class)
public class BreezeMixin implements SetActiveTargetMob {

    @Unique
    private boolean mobbattle_active_targeting;

    @Inject(method = "canAttackType", at = @At("HEAD"), cancellable = true)
    private void targetWarden(EntityType<?> entityType, CallbackInfoReturnable<Boolean> info) {
        if (this.mobbattle_active_targeting)
            info.setReturnValue(true);
    }

    @Override
    public void setTargeting(boolean flag) {
        this.mobbattle_active_targeting = flag;
    }
}
