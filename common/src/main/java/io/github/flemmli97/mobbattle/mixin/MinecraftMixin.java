package io.github.flemmli97.mobbattle.mixin;

import io.github.flemmli97.mobbattle.client.ClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void itemHighlight(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (ClientHandler.handleEntityHighlight(entity))
            info.setReturnValue(true);
    }
}
