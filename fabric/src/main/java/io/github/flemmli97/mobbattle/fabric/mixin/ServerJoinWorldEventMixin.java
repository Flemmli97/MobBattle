package io.github.flemmli97.mobbattle.fabric.mixin;

import io.github.flemmli97.mobbattle.fabric.handler.EventHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentEntitySectionManager.class)
public class ServerJoinWorldEventMixin {

    @Inject(method = "addEntity", at = @At(value = "HEAD"))
    private void entityJoin(EntityAccess entityAccess, boolean bl, CallbackInfoReturnable<Boolean> info) {
        if (entityAccess instanceof Entity)
            EventHandler.addTeamTarget((Entity) entityAccess);
    }
}
