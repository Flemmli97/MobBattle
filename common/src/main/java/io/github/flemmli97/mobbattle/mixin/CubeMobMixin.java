package io.github.flemmli97.mobbattle.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractCubeMob.class)
public abstract class CubeMobMixin {

    @Inject(method = "push", at = @At("RETURN"))
    private void onPush(Entity entity, CallbackInfo info) {
        if (entity == ((AbstractCubeMob) (Object) this).getTarget() && this.canDealDamage())
            this.dealDamage((LivingEntity) entity);
    }

    @Shadow
    protected abstract void dealDamage(LivingEntity target);

    @Shadow
    protected abstract boolean canDealDamage();
}
