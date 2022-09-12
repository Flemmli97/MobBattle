package io.github.flemmli97.mobbattle.fabric.mixin;

import io.github.flemmli97.mobbattle.fabric.handler.EventHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "hurt", at = @At(value = "HEAD"), cancellable = true)
    private void livingHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (!EventHandler.teamFriendlyFire((LivingEntity) (Object) this, source, amount)) {
            info.setReturnValue(false);
            info.cancel();
        }
    }

    /**
     * Vanilla sets it in Mob#doHurtTarget but all mobs that override that method dont so...
     */
    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private void mobSetHurt(DamageSource source, float amount, CallbackInfo info) {
        if (source.getEntity() instanceof Mob mob)
            mob.setLastHurtMob((LivingEntity) (Object) this);
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void livingTick(CallbackInfo info) {
        EventHandler.livingTick((LivingEntity) (Object) this);
    }
}
