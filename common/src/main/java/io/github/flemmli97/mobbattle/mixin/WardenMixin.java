package io.github.flemmli97.mobbattle.mixin;

import io.github.flemmli97.mobbattle.common.utils.ActiveTargetMobbattle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Warden.class)
public class WardenMixin {

    @Inject(method = "canTargetEntity", at = @At("HEAD"), cancellable = true)
    private void targetWarden(@Nullable Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (((ActiveTargetMobbattle) this).mobbattle$IsActiveTargeting() && entity instanceof LivingEntity living && living.getType() == EntityType.WARDEN)
            info.setReturnValue(true);
    }
}
