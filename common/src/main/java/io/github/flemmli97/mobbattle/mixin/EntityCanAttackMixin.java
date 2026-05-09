package io.github.flemmli97.mobbattle.mixin;

import io.github.flemmli97.mobbattle.common.utils.ActiveTargetMobbattle;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.breeze.Breeze;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Breeze.class, Mob.class})
public class EntityCanAttackMixin {

    @Inject(method = "canAttack", at = @At("HEAD"), cancellable = true)
    private void targetWarden(LivingEntity target, CallbackInfoReturnable<Boolean> info) {
        if (((ActiveTargetMobbattle) this).mobbattle$IsActiveTargeting())
            info.setReturnValue(true);
    }
}
