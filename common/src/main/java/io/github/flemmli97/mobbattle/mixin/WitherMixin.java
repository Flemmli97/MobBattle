package io.github.flemmli97.mobbattle.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.flemmli97.mobbattle.common.utils.ActiveTargetMobbattle;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WitherBoss.class)
public class WitherMixin {

    @ModifyExpressionValue(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean allowSource(boolean original, ServerLevel level, DamageSource source) {
        if (source.getEntity() instanceof LivingEntity living && ((ActiveTargetMobbattle) living).mobbattle$IsActiveTargeting())
            return false;
        return original;
    }
}
