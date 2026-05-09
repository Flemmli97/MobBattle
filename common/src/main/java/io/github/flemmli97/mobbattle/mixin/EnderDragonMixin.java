package io.github.flemmli97.mobbattle.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.flemmli97.mobbattle.common.utils.ActiveTargetMobbattle;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderDragon.class)
public class EnderDragonMixin {

    @ModifyExpressionValue(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean allowSource(boolean original, ServerLevel level, EnderDragonPart part, DamageSource source) {
        if (source.getEntity() instanceof LivingEntity living && ((ActiveTargetMobbattle) living).mobbattle$IsActiveTargeting())
            return true;
        return original;
    }
}
