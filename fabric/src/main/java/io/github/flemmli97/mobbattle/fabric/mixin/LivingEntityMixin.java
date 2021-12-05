package io.github.flemmli97.mobbattle.fabric.mixin;

import io.github.flemmli97.mobbattle.fabric.handler.EventHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "hurt", at = @At(value = "HEAD"), cancellable = true)
    private void livingHurt(DamageSource arg, float amount, CallbackInfoReturnable<Boolean> info) {
        if (!EventHandler.teamFriendlyFire((LivingEntity) (Object) this, arg, amount)) {
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void livingTick(CallbackInfo info) {
        EventHandler.livingTick((LivingEntity) (Object) this);
    }
}
